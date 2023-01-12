use crate::api::stream::InputStream;
use crate::{log, warn};

pub struct DataInputStream {
    pub(crate) inner: Box<dyn InputStream>,
    little_endian: bool,
}
impl DataInputStream {
    pub fn from(inner: Box<dyn InputStream>, little_endian: bool) -> Self {
        Self { inner, little_endian }
    }
}

impl InputStream for DataInputStream {
    fn read_one(&mut self) -> Option<u8> {
        self.inner.read_one()
    }

    fn read_some(&mut self, buffer: &mut [u8]) -> usize {
        self.inner.read_some(buffer)
    }

    fn read_some_there(&mut self, buffer: &mut [u8], offset: usize, length: usize) -> usize {
        self.inner.read_some_there(buffer, offset, length)
    }

    fn read_some_here(&mut self, n: usize) -> Vec<u8> {
        self.inner.read_some_here(n)
    }

    fn skip(&mut self, n: usize) -> usize {
        self.inner.skip(n)
    }

    fn get_position(&self) -> u64 {
        self.inner.get_position()
    }

    fn is_eof(&self) -> bool {
        self.inner.is_eof()
    }
}

impl DataInputStream {
    pub fn read_endian(&mut self, n: u8) -> Option<u64> {
        let mut result = 0;
        let mut i = 0;
        let mut bytes = vec![0; n as usize];
        if self.read_some(&mut bytes) != n as usize {
            return None
        }

        while i < n {
            let byte = bytes[i as usize];
            if self.little_endian {
                result |= (byte as u64) << (i * 8);
            } else {
                result |= (byte as u64) << ((n - i - 1) * 8);
            }
            i += 1;
        }
        Some(result)
    }
    pub fn read_bytes(&mut self, n: usize) -> Option<u64> {
        if n > 8 {
            panic!("Cannot read more than 8 bytes");
        }
        self.read_endian(n as u8)
    }
    pub fn read_u8(&mut self) -> Option<u8> {
        self.read_bytes(1).map(|x| x as u8)
    }
    pub fn read_i8(&mut self) -> Option<i8> {
        self.read_endian(1).map(|x| x as i8)
    }
    pub fn read_u16(&mut self) -> Option<u16> {
        self.read_endian(2).map(|x| x as u16)
    }
    pub fn read_i16(&mut self) -> Option<i16> {
        self.read_endian(2).map(|x| x as i16)
    }
    pub fn read_u32(&mut self) -> Option<u32> {
        self.read_endian(4).map(|x| x as u32)
    }
    pub fn read_i32(&mut self) -> Option<i32> {
        self.read_endian(4).map(|x| x as i32)
    }
    pub fn read_u64(&mut self) -> Option<u64> {
        self.read_endian(8)
    }
    pub fn read_i64(&mut self) -> Option<i64> {
        self.read_endian(8).map(|x| x as i64)
    }
    pub fn read_f32(&mut self) -> Option<f32> {
        self.read_endian(4).map(|x| x as f32)
    }
    pub fn read_f64(&mut self) -> Option<f64> {
        self.read_endian(8).map(|x| x as f64)
    }
    pub fn read_string(&mut self, length: usize) -> String {
        let mut buffer = vec![0; length];
        if self.read_some(&mut buffer) != length {
            warn!("String length mismatch, returning what could be read. May be malformed. (expected {}, got {})", length, buffer.len());
        }
        String::from_utf8(buffer).unwrap()
    }
    pub fn read_string_null_terminated(&mut self) -> String {
        let mut bytes = vec![];
        loop {
            let byte = self.read_u8();
            if byte.is_none() {
                break;
            }
            let byte = byte.unwrap();
            if byte == 0 {
                break;
            }
            bytes.push(byte);
        }
        log!("Read {} bytes for null terminated string", bytes.len());
        String::from_utf8(bytes).unwrap()
    }
}