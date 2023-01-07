use crate::api::stream::{InputStream, Seekable, SeekableInputStream};

pub struct BufferInputStream {
    buffer: Vec<u8>,
    position: usize,
}
impl BufferInputStream {
    pub fn new(buffer: Vec<u8>) -> BufferInputStream {
        BufferInputStream {
            buffer,
            position: 0,
        }
    }
}

impl InputStream for BufferInputStream {
    fn read_one(&mut self) -> u8 {
        let byte = self.buffer[self.position];
        self.position += 1;
        byte
    }

    fn read_some(&mut self, buffer: &mut [u8]) -> usize {
        let length = buffer.len();
        let mut i = 0;
        while i < length {
            buffer[i] = self.read_one();
            i += 1;
        }
        length
    }

    fn read_some_there(&mut self, buffer: &mut [u8], offset: usize, length: usize) -> usize {
        let mut i = 0;
        while i < length {
            buffer[offset + i] = self.read_one();
            i += 1;
        }
        length
    }

    fn read_some_here(&mut self, n: usize) -> Vec<u8> {
        let mut buffer = vec![0; n];
        self.read_some(&mut buffer);
        buffer
    }

    fn skip(&mut self, n: usize) {
        self.position += n;
    }

    fn get_position(&self) -> u64 {
        self.position as u64
    }
}

impl Seekable for BufferInputStream {
    fn seek(&mut self, position: u64) {
        self.position = position as usize;
    }

    fn length(&self) -> u64 {
        self.buffer.len() as u64
    }

    fn position(&self) -> u64 {
        self.position as u64
    }
}

impl SeekableInputStream for BufferInputStream {

}