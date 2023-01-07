pub trait Seekable {
    fn seek(&mut self, position: u64);
    fn length(&self) -> u64;
    fn position(&self) -> u64;
}