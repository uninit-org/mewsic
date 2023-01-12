use crate::api::stream::{InputStream, Seekable, SeekableInputStream};
use crate::warn;

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
    fn read_one(&mut self) -> Option<u8> {
        if self.position >= self.buffer.len() {
            None
        } else {
            let result = self.buffer[self.position];
            self.position += 1;
            Some(result)
        }
    }

    fn read_some(&mut self, buffer: &mut [u8]) -> usize {
        let length = buffer.len();
        let mut i = 0;
        while i < length {
            if let Some(byte) = self.read_one() {
                buffer[i] = byte;
            } else {
                return i;
            }
            i += 1;
        }
        length
    }

    fn read_some_there(&mut self, buffer: &mut [u8], offset: usize, length: usize) -> usize {
        let mut i = 0;
        while i < length {
            if let Some(byte) = self.read_one() {
                buffer[offset + i] = byte;
            } else {
                return i;
            }
            i += 1;
        }
        length
    }

    fn read_some_here(&mut self, n: usize) -> Vec<u8> {
        let mut result = vec![0; n];
        let int_read = self.read_some(&mut result);
        if int_read != n {
            warn!("Not enough bytes to fulfill read_some_here call, returning vector with as many bytes as possible (requested: {}, actual: {})", n, int_read);
        }
        result[0..int_read].to_vec()
    }

    fn skip(&mut self, n: usize) -> usize {
        let mut i = 0;
        while i < n {
            if self.read_one().is_none() {
                return i;
            }
            i += 1;
        }
        n
    }

    fn get_position(&self) -> u64 {
        self.position as u64
    }

    fn is_eof(&self) -> bool {
        self.position >= self.buffer.len()
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