use crate::api::stream::{InputStream, Seekable};

pub trait SeekableInputStream: InputStream + Seekable {}