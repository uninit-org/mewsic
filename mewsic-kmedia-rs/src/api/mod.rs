pub mod stream;

pub trait AudioMedia {}
pub trait VideoMedia {}
pub trait ContainerMedia {
    fn get_metadata(&self) -> Box<dyn ContainerMetadata>;
}
pub trait ContainerMetadata {}