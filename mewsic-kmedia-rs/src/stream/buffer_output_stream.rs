use crate::api::stream::{OutputStream, Seekable, SeekableOutputStream};

pub struct BufferOutputStream {
    buffer: Vec<u8>,
    position: usize,
}
impl BufferOutputStream {
    pub fn new() -> BufferOutputStream {
        BufferOutputStream {
            buffer: Vec::new(),
            position: 0,
        }
    }
    pub fn get_buffer(&self) -> &Vec<u8> {
        &self.buffer
    }

}

impl OutputStream for BufferOutputStream {
    fn write_one(&mut self, byte: u8) {
        if self.position == self.buffer.len() {
            self.buffer.push(byte);
        } else {
            self.buffer[self.position] = byte;
        }
    }

    fn write_some(&mut self, buffer: &[u8]) {
        let length = buffer.len();
        let mut i = 0;
        while i < length {
            self.write_one(buffer[i]);
            i += 1;
        }
    }

    fn write_some_there(&mut self, buffer: &[u8], offset: usize, length: usize) {
        let mut i = 0;
        while i < length {
            self.write_one(buffer[offset + i]);
            i += 1;
        }
    }

    fn skip(&mut self, n: usize) {
        self.position += n;
    }
}

impl Seekable for BufferOutputStream {
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

impl SeekableOutputStream for BufferOutputStream {}