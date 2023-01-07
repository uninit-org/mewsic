use crate::api::{ContainerMedia, ContainerMetadata};
use crate::container::mpeg4::{Mpeg4BoxSignatures, Mpeg4Metadata};

pub struct Mpeg4Container {

}

impl Mpeg4Container {

}
impl ContainerMedia for Mpeg4Container {
    fn get_metadata(&self) -> Box<dyn ContainerMetadata> {

        Box::new(Mpeg4Metadata {})
    }
}