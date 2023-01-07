use crate::api::stream::{OutputStream, Seekable};

pub trait SeekableOutputStream: OutputStream + Seekable {}