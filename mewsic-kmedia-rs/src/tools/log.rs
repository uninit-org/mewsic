#[macro_export]
macro_rules! log {
    (
        $($arg:expr),*
    ) => {
        println!("[LOG] {}", format_args!($($arg, )*));
    };
}
#[macro_export]
macro_rules! warn {
    (
        $($arg:expr),*
    ) => {
        println!("[WARN] {}", format_args!($($arg, )*));
    };
}
#[macro_export]
macro_rules! error {
    (
        $($arg:expr),*
    ) => {
        println!("[ERROR] {}", format_args!($($arg, )*));
    };
}
