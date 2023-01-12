pub trait InputStream {
    fn read_one(&mut self) -> Option<u8>;
    fn read_some(&mut self, buffer: &mut [u8]) -> usize;
    fn read_some_there(&mut self, buffer: &mut [u8], offset: usize, length: usize) -> usize;
    fn read_some_here(&mut self, n: usize) -> Vec<u8>;
    fn skip(&mut self, n: usize) -> usize;
    fn get_position(&self) -> u64;
    fn is_eof(&self) -> bool;
}