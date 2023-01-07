use crate::api::stream::InputStream;

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
    fn read_one(&mut self) -> u8 {
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

    fn skip(&mut self, n: usize) {
        self.inner.skip(n)
    }

    fn get_position(&self) -> u64 {
        self.inner.get_position()
    }
}

impl DataInputStream {
    pub fn read_endian(&mut self, n: u8) -> u64 {
        let mut result = 0;
        let mut i = 0;
        while i < n {
            let byte = self.read_one();
            if self.little_endian {
                result |= (byte as u64) << (i * 8);
            } else {
                result |= (byte as u64) << ((n - i - 1) * 8);
            }
            i += 1;
        }
        result
    }
    pub fn read_bytes(&mut self, n: usize) -> u64 {
        if n > 8 {
            panic!("Cannot read more than 8 bytes");
        }
        self.read_endian(n as u8)
    }
    pub fn read_u8(&mut self) -> u8 {
        self.read_one()
    }
    pub fn read_i8(&mut self) -> i8 {
        self.read_one() as i8
    }
    pub fn read_u16(&mut self) -> u16 {
        self.read_endian(2) as u16
    }
    pub fn read_i16(&mut self) -> i16 {
        self.read_endian(2) as i16
    }
    pub fn read_u32(&mut self) -> u32 {
        self.read_endian(4) as u32
    }
    pub fn read_i32(&mut self) -> i32 {
        self.read_endian(4) as i32
    }
    pub fn read_u64(&mut self) -> u64 {
        self.read_endian(8) as u64
    }
    pub fn read_i64(&mut self) -> i64 {
        self.read_endian(8) as i64
    }
    pub fn read_f32(&mut self) -> f32 {
        self.read_endian(4) as f32
    }
    pub fn read_f64(&mut self) -> f64 {
        self.read_endian(8) as f64
    }
    pub fn read_string(&mut self, length: usize) -> String {
        let bytes = self.read_some_here(length);
        String::from_utf8(bytes).unwrap()
    }
    pub fn read_string_null_terminated(&mut self) -> String {
        let mut bytes = vec![];
        let mut byte = self.read_one();
        while byte != 0 {
            bytes.push(byte);
            byte = self.read_one();
        }
        String::from_utf8(bytes).unwrap()
    }
}