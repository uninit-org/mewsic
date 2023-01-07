mod log;

use std::{u32, u64};
pub fn sig(signature: String) -> u64 {
    // String -> [u8; 4] -> i64
    let mut sig = [0u8; 4];
    sig.copy_from_slice(signature.as_bytes());
    u64::from_be_bytes(sig)
}
pub fn longFromBytes(bytes: &[u8]) -> u64 {
    let mut buf = [0u8; 4];
    buf.copy_from_slice(bytes);
    u64::from_be_bytes(buf)
}
mod tests {
    use super::*;
    #[test]
    pub fn sig_works() {
        assert_eq!(sig("meco".to_string()), 1835361135 as u32);
    }
}