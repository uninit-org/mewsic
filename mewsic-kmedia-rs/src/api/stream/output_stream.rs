/// Data Output Stream
pub trait OutputStream {
    fn write_one(&mut self, byte: u8);
    fn write_some(&mut self, buffer: &[u8]);
    fn write_some_there(&mut self, buffer: &[u8], offset: usize, length: usize);
    fn skip(&mut self, n: usize);
}