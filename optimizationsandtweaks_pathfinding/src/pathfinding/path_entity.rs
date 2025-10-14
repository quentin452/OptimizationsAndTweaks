use super::path_point::PathPoint;

/// Represents a complete path from start to destination
/// Contains an array of PathPoints that define the route
#[derive(Debug, Clone)]
pub struct PathEntity {
    /// The actual points in the path
    pub points: Vec<PathPoint>,
    /// PathEntity Array Index the Entity is currently targeting
    pub current_path_index: usize,
    /// The total length of the path
    pub path_length: usize,
}

impl PathEntity {
    /// Creates a new PathEntity from a vector of PathPoints
    pub fn new(points: Vec<PathPoint>) -> Self {
        let path_length = points.len();
        PathEntity {
            points,
            current_path_index: 0,
            path_length,
        }
    }

    /// Directs this path to the next point in its array
    pub fn increment_path_index(&mut self) {
        self.current_path_index += 1;
    }

    /// Returns true if this path has reached the end
    pub fn is_finished(&self) -> bool {
        self.current_path_index >= self.path_length
    }

    /// Returns the last PathPoint of the array
    pub fn get_final_path_point(&self) -> Option<&PathPoint> {
        if self.path_length > 0 {
            self.points.get(self.path_length - 1)
        } else {
            None
        }
    }

    /// Returns the PathPoint located at the specified PathIndex
    pub fn get_path_point_from_index(&self, index: usize) -> Option<&PathPoint> {
        self.points.get(index)
    }

    /// Gets the current path length
    pub fn get_current_path_length(&self) -> usize {
        self.path_length
    }

    /// Sets the current path length
    pub fn set_current_path_length(&mut self, length: usize) {
        self.path_length = length.min(self.points.len());
    }

    /// Gets the current path index
    pub fn get_current_path_index(&self) -> usize {
        self.current_path_index
    }

    /// Sets the current path index
    pub fn set_current_path_index(&mut self, index: usize) {
        self.current_path_index = index;
    }

    /// Gets the vector position from the given index
    /// Returns (x, y, z) adjusted for entity width
    pub fn get_vector_from_index(&self, index: usize, entity_width: f32) -> Option<(f64, f64, f64)> {
        self.points.get(index).map(|point| {
            let width_offset = ((entity_width + 1.0) as i32) as f64 * 0.5;
            let x = point.x_coord as f64 + width_offset;
            let y = point.y_coord as f64;
            let z = point.z_coord as f64 + width_offset;
            (x, y, z)
        })
    }

    /// Returns the current PathEntity target node as a vector
    pub fn get_position(&self, entity_width: f32) -> Option<(f64, f64, f64)> {
        self.get_vector_from_index(self.current_path_index, entity_width)
    }

    /// Returns true if the EntityPaths are the same (non-instance related equals)
    pub fn is_same_path(&self, other: &PathEntity) -> bool {
        if self.points.len() != other.points.len() {
            return false;
        }

        for (i, point) in self.points.iter().enumerate() {
            if let Some(other_point) = other.points.get(i) {
                if point.x_coord != other_point.x_coord
                    || point.y_coord != other_point.y_coord
                    || point.z_coord != other_point.z_coord
                {
                    return false;
                }
            } else {
                return false;
            }
        }

        true
    }

    /// Returns true if the final PathPoint in the PathEntity equals the given coordinates
    pub fn is_destination_same(&self, x: f64, _y: f64, z: f64) -> bool {
        if let Some(final_point) = self.get_final_path_point() {
            final_point.x_coord == x as i32 && final_point.z_coord == z as i32
        } else {
            false
        }
    }

    /// Returns the total number of points in the path
    pub fn len(&self) -> usize {
        self.points.len()
    }

    /// Returns true if the path is empty
    pub fn is_empty(&self) -> bool {
        self.points.is_empty()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_path_entity_creation() {
        let points = vec![
            PathPoint::new(0, 0, 0),
            PathPoint::new(1, 0, 1),
            PathPoint::new(2, 0, 2),
        ];
        
        let path = PathEntity::new(points);
        assert_eq!(path.len(), 3);
        assert_eq!(path.get_current_path_index(), 0);
        assert!(!path.is_finished());
    }

    #[test]
    fn test_path_navigation() {
        let points = vec![
            PathPoint::new(0, 0, 0),
            PathPoint::new(1, 0, 1),
        ];
        
        let mut path = PathEntity::new(points);
        assert!(!path.is_finished());
        
        path.increment_path_index();
        assert!(!path.is_finished());
        
        path.increment_path_index();
        assert!(path.is_finished());
    }

    #[test]
    fn test_final_path_point() {
        let points = vec![
            PathPoint::new(0, 0, 0),
            PathPoint::new(1, 0, 1),
            PathPoint::new(2, 0, 2),
        ];
        
        let path = PathEntity::new(points);
        let final_point = path.get_final_path_point().unwrap();
        assert_eq!(final_point.x_coord, 2);
        assert_eq!(final_point.y_coord, 0);
        assert_eq!(final_point.z_coord, 2);
    }

    #[test]
    fn test_is_same_path() {
        let points1 = vec![
            PathPoint::new(0, 0, 0),
            PathPoint::new(1, 0, 1),
        ];
        
        let points2 = vec![
            PathPoint::new(0, 0, 0),
            PathPoint::new(1, 0, 1),
        ];
        
        let points3 = vec![
            PathPoint::new(0, 0, 0),
            PathPoint::new(2, 0, 2),
        ];
        
        let path1 = PathEntity::new(points1);
        let path2 = PathEntity::new(points2);
        let path3 = PathEntity::new(points3);
        
        assert!(path1.is_same_path(&path2));
        assert!(!path1.is_same_path(&path3));
    }

    #[test]
    fn test_is_destination_same() {
        let points = vec![
            PathPoint::new(0, 0, 0),
            PathPoint::new(5, 10, 15),
        ];
        
        let path = PathEntity::new(points);
        assert!(path.is_destination_same(5.0, 10.0, 15.0));
        assert!(!path.is_destination_same(0.0, 0.0, 0.0));
    }
}
