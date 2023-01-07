use crate::api::stream::OutputStream;

pub struct DataOutputStream {
    stream: Box<dyn OutputStream>,
    little_endian: bool,
}
impl DataOutputStream {
    pub fn new(stream: Box<dyn OutputStream>, little_endian: bool) -> Self {
        Self { stream, little_endian }
    }
}
impl OutputStream for DataOutputStream {
    fn write_one(&mut self, byte: u8) {
        self.stream.write_one(byte);
    }
    fn write_some(&mut self, buffer: &[u8]) {
        self.stream.write_some(buffer);
    }
    fn write_some_there(&mut self, buffer: &[u8], offset: usize, length: usize) {
        self.stream.write_some_there(buffer, offset, length);
    }
    fn skip(&mut self, n: usize) {
        self.stream.skip(n);
    }
}
impl DataOutputStream {
    pub fn write_endian(&mut self, n: u8, value: u64) {
        let mut i = 0;
        while i < n {
            let byte = if self.little_endian {
                (value >> (i * 8)) as u8
            } else {
                (value >> ((n - i - 1) * 8)) as u8
            };
            self.write_one(byte);
            i += 1;
        }
    }
    pub fn write_u8(&mut self, value: u8) {
        self.write_one(value);
    }
    pub fn write_i8(&mut self, value: i8) {
        self.write_one(value as u8);
    }
    pub fn write_u16(&mut self, value: u16) {
        self.write_endian(2, value as u64);
    }
    pub fn write_i16(&mut self, value: i16) {
        self.write_endian(2, value as u64);
    }
    pub fn write_u32(&mut self, value: u32) {
        self.write_endian(4, value as u64);
    }
    pub fn write_i32(&mut self, value: i32) {
        self.write_endian(4, value as u64);
    }
    pub fn write_u64(&mut self, value: u64) {
        self.write_endian(8, value);
    }
    pub fn write_i64(&mut self, value: i64) {
        self.write_endian(8, value as u64);
    }
}
