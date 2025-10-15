 use super::path_point::PathPoint;

/// A priority queue implementation for pathfinding that stores only point hashes.
/// All PathPoint data lives in an external map; the heap references them by hash
/// and compares/prioritizes via a provided distance accessor closure.
pub struct Path {
    /// Binary heap storage of point hashes (min-heap by distance_to_target)
    heap: Vec<Option<i32>>, // stores PathPoint.hash
    /// The number of hashes currently stored
    count: usize,
}

impl Path {
    /// Creates a new empty Path with initial capacity
    pub fn new() -> Self {
        Path {
            heap: vec![None; 1024],
            count: 0,
        }
    }

    /// Aggressively clear and shrink the internal heap storage to a sane baseline
    /// Call this after a pathfinding run to avoid retaining large allocations
    pub fn clear_and_shrink(&mut self) {
        self.count = 0;
        // Reinitialize to baseline capacity to drop excess capacity
        self.heap = vec![None; 1024];
    }

    /// Clears the path
    pub fn clear_path(&mut self) {
        self.count = 0;
        // If the heap grew large in the previous search, shrink it to a sane baseline
        // to avoid retaining memory across calls.
        if self.heap.len() > 4096 {
            // Reinitialize to baseline capacity
            self.heap = vec![None; 1024];
        } else {
            // Clear references in place
            for i in 0..self.heap.len() {
                self.heap[i] = None;
            }
        }
    }

    /// Adds a point hash to the heap using the provided distance accessor.
    /// Returns the index where it was inserted.
    pub fn add_point_hash<F>(&mut self, hash: i32, mut get_distance: F) -> Result<usize, &'static str>
    where
        F: FnMut(i32) -> f32,
    {
        // Reject duplicates that are already in the heap within current count
        if self.heap.iter().take(self.count).any(|h| h.map_or(false, |x| x == hash)) {
            return Err("Point with same hash already in path!");
        }

        if self.count == self.heap.len() {
            let new_capacity = if self.count == 0 { 1 } else { self.count << 1 };
            self.heap.resize(new_capacity, None);
        }

        let idx = self.count;
        self.heap[idx] = Some(hash);
        self.count += 1;

        // Percolate up based on distance comparator
        self.sort_back_with(idx, &mut get_distance);
        Ok(idx)
    }

    /// Re-heapify the position of a point given its hash after an external
    /// distance change. Returns false if the hash is not found.
    pub fn reheapify_by_hash<F>(&mut self, hash: i32, mut get_distance: F) -> bool
    where
        F: FnMut(i32) -> f32,
    {
        // Locate the index of the hash in the heap
        let mut idx_opt: Option<usize> = None;
        for i in 0..self.count {
            if let Some(h) = self.heap[i] {
                if h == hash {
                    idx_opt = Some(i);
                    break;
                }
            }
        }
        let Some(idx) = idx_opt else { return false; };

        // Try both directions; one of them will be a no-op.
        self.sort_back_with(idx, &mut get_distance);
        self.sort_forward_with(idx, &mut get_distance);
        true
    }

    /// Returns and removes the root hash (minimum by distance)
    pub fn dequeue<F>(&mut self, mut get_distance: F) -> Option<i32>
    where
        F: FnMut(i32) -> f32,
    {
        if self.count == 0 {
            return None;
        }

        // Take root
        let result = self.heap[0].take()?;

        // Move last to root and re-heapify
        self.count -= 1;
        if self.count > 0 {
            self.heap[0] = self.heap[self.count].take();
            self.heap[self.count] = None;
            self.sort_forward_with(0, &mut get_distance);
        }

        Some(result)
    }

    /// Returns true if this path contains no points
    pub fn is_path_empty(&self) -> bool {
        self.count == 0
    }

    /// Returns the number of points in the path
    pub fn len(&self) -> usize {
        self.count
    }

    /// Internal: percolate a node up as long as it's smaller than parent (min-heap)
    fn sort_back_with<F>(&mut self, mut index: usize, get_distance: &mut F)
    where
        F: FnMut(i32) -> f32,
    {
        if index >= self.count { return; }

        while index > 0 {
            let parent_index = (index - 1) >> 1;
            let Some(cur_h) = self.heap[index] else { return; };
            let Some(par_h) = self.heap[parent_index] else { break; };

            let point_distance = get_distance(cur_h);
            let parent_distance = get_distance(par_h);

            if point_distance >= parent_distance { break; }

            // Swap with parent
            self.heap.swap(index, parent_index);
            index = parent_index;
        }
    }

    /// Internal: percolate a node down as long as child has smaller distance
    fn sort_forward_with<F>(&mut self, mut index: usize, get_distance: &mut F)
    where
        F: FnMut(i32) -> f32,
    {
        if index >= self.count { return; }

        loop {
            let left_child = 1 + (index << 1);
            let right_child = left_child + 1;

            if left_child >= self.count { break; }

            let Some(cur_h) = self.heap[index] else { return; };
            let Some(left_h) = self.heap[left_child] else { break; };

            let left_distance = get_distance(left_h);
            let (swap_child, swap_distance) = if right_child >= self.count {
                (left_child, left_distance)
            } else {
                let right_distance = if let Some(right_h) = self.heap[right_child] {
                    get_distance(right_h)
                } else {
                    f32::INFINITY
                };
                if left_distance < right_distance {
                    (left_child, left_distance)
                } else {
                    (right_child, right_distance)
                }
            };

            let cur_distance = get_distance(cur_h);
            if swap_distance >= cur_distance { break; }

            // Swap with chosen child
            self.heap.swap(index, swap_child);
            index = swap_child;
        }
    }

    #[cfg(test)]
    pub(crate) fn debug_capacity(&self) -> usize {
        self.heap.len()
    }

    /// Count number of occurrences of a hash in the heap (for diagnostics/tests)
    #[cfg(test)]
    pub(crate) fn debug_count_hash(&self, hash: i32) -> usize {
        let mut n = 0;
        for i in 0..self.count {
            if let Some(h) = self.heap[i] {
                if h == hash { n += 1; }
            }
        }
        n
    }
}

impl Default for Path {
    fn default() -> Self {
        Self::new()
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::collections::HashMap;

    #[test]
    fn test_path_creation() {
        let path = Path::new();
        assert!(path.is_path_empty());
        assert_eq!(path.len(), 0);
    }

    #[test]
    fn test_add_and_dequeue_by_hash() {
        let mut path = Path::new();
        let mut dist: HashMap<i32, f32> = HashMap::new();

        let p1 = PathPoint::new(0, 0, 0); dist.insert(p1.hash, 10.0);
        let p2 = PathPoint::new(1, 1, 1); dist.insert(p2.hash, 5.0);
        let p3 = PathPoint::new(2, 2, 2); dist.insert(p3.hash, 15.0);

        let getd = |h: i32| *dist.get(&h).unwrap();

        path.add_point_hash(p1.hash, getd).unwrap();
        path.add_point_hash(p2.hash, getd).unwrap();
        path.add_point_hash(p3.hash, getd).unwrap();

        assert_eq!(path.len(), 3);

        let first = path.dequeue(getd).unwrap();
        assert_eq!(first, p2.hash);

        let second = path.dequeue(getd).unwrap();
        assert_eq!(second, p1.hash);

        let third = path.dequeue(getd).unwrap();
        assert_eq!(third, p3.hash);

        assert!(path.is_path_empty());
    }

    #[test]
    fn test_clear_path() {
        let mut path = Path::new();
        let mut dist = |_: i32| 1.0;
        path.add_point_hash(PathPoint::new(0, 0, 0).hash, &mut dist).unwrap();
        assert!(!path.is_path_empty());
        path.clear_path();
        assert!(path.is_path_empty());
    }

    #[test]
    fn test_no_duplicate_hash() {
        let mut path = Path::new();
        let mut dist = |_: i32| 1.0;
        let h = PathPoint::new(1, 2, 3).hash;
        let _ = path.add_point_hash(h, &mut dist);
        let dup = path.add_point_hash(h, &mut dist);
        assert!(dup.is_err());
    }

    #[test]
    fn test_clear_and_shrink_resets_capacity() {
        let mut path = Path::new();
        assert_eq!(path.debug_capacity(), 1024);
        // Force growth
        let mut dist = |_: i32| 1.0;
        for i in 0..5000 {
            let h = PathPoint::new(i, 0, 0).hash;
            let _ = path.add_point_hash(h, &mut dist);
        }
        assert!(path.debug_capacity() >= 8192);
        // Shrink back to baseline
        path.clear_and_shrink();
        assert_eq!(path.debug_capacity(), 1024);
        assert!(path.is_path_empty());
    }
}
