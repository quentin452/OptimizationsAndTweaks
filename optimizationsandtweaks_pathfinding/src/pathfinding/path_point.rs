/// A point in a pathfinding path
/// Represents a 3D coordinate with pathfinding metadata
#[derive(Debug, Clone, Copy)]
pub struct PathPoint {
    /// The x coordinate of this point
    pub x_coord: i32,
    /// The y coordinate of this point
    pub y_coord: i32,
    /// The z coordinate of this point
    pub z_coord: i32,
    /// A hash of the coordinates used to identify this point
    pub hash: i32,
    /// The index of this point in its assigned path
    pub index: i32,
    /// The distance along the path to this point
    pub total_path_distance: f32,
    /// The linear distance to the next point
    pub distance_to_next: f32,
    /// The distance to the target
    pub distance_to_target: f32,
    /// The point preceding this in its assigned path (stored as hash key)
    pub previous_hash: Option<i32>,
    /// Indicates this is the origin
    pub is_first: bool,
}

impl PathPoint {
    /// Creates a new PathPoint at the given coordinates
    pub fn new(x: i32, y: i32, z: i32) -> Self {
        PathPoint {
            x_coord: x,
            y_coord: y,
            z_coord: z,
            hash: Self::make_hash(x, y, z),
            index: -1,
            total_path_distance: 0.0,
            distance_to_next: 0.0,
            distance_to_target: 0.0,
            previous_hash: None,
            is_first: false,
        }
    }

    /// Creates a hash from coordinates
    /// This matches the Java implementation's hash function
    pub fn make_hash(x: i32, y: i32, z: i32) -> i32 {
        let mut hash = (y & 255) | ((x & 32767) << 8) | ((z & 32767) << 24);
        
        if x < 0 {
            hash |= i32::MIN;
        }
        
        if z < 0 {
            hash |= 32768;
        }
        
        hash
    }

    /// Returns the linear distance to another path point
    pub fn distance_to(&self, other: &PathPoint) -> f32 {
        let dx = (other.x_coord - self.x_coord) as f32;
        let dy = (other.y_coord - self.y_coord) as f32;
        let dz = (other.z_coord - self.z_coord) as f32;
        (dx * dx + dy * dy + dz * dz).sqrt()
    }

    /// Returns the squared distance to another path point
    pub fn distance_to_squared(&self, other: &PathPoint) -> f32 {
        let dx = (other.x_coord - self.x_coord) as f32;
        let dy = (other.y_coord - self.y_coord) as f32;
        let dz = (other.z_coord - self.z_coord) as f32;
        dx * dx + dy * dy + dz * dz
    }

    /// Returns true if this point has already been assigned to a path
    pub fn is_assigned(&self) -> bool {
        self.index >= 0
    }
}

impl PartialEq for PathPoint {
    fn eq(&self, other: &Self) -> bool {
        self.hash == other.hash 
            && self.x_coord == other.x_coord 
            && self.y_coord == other.y_coord 
            && self.z_coord == other.z_coord
    }
}

impl Eq for PathPoint {}

impl std::hash::Hash for PathPoint {
    fn hash<H: std::hash::Hasher>(&self, state: &mut H) {
        self.hash.hash(state);
    }
}

impl std::fmt::Display for PathPoint {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}, {}, {}", self.x_coord, self.y_coord, self.z_coord)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_path_point_creation() {
        let point = PathPoint::new(10, 20, 30);
        assert_eq!(point.x_coord, 10);
        assert_eq!(point.y_coord, 20);
        assert_eq!(point.z_coord, 30);
        assert_eq!(point.index, -1);
        assert!(!point.is_assigned());
    }

    #[test]
    fn test_distance_calculation() {
        let p1 = PathPoint::new(0, 0, 0);
        let p2 = PathPoint::new(3, 4, 0);
        assert_eq!(p1.distance_to(&p2), 5.0);
        assert_eq!(p1.distance_to_squared(&p2), 25.0);
    }

    #[test]
    fn test_hash_consistency() {
        let p1 = PathPoint::new(10, 20, 30);
        let p2 = PathPoint::new(10, 20, 30);
        assert_eq!(p1.hash, p2.hash);
        assert_eq!(p1, p2);
    }
}

impl Default for PathPoint {
    fn default() -> Self {
        PathPoint {
            x_coord: 0,
            y_coord: 0,
            z_coord: 0,
            hash: PathPoint::make_hash(0, 0, 0),
            index: -1,
            total_path_distance: 0.0,
            distance_to_next: 0.0,
            distance_to_target: 0.0,
            previous_hash: None,
            is_first: false,
        }
    }
}
