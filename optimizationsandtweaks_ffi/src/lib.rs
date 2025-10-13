use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;

/// Prints "Hello World from Rust!" to stdout
/// JNI signature: ()V
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustFFI_rust_1hello_1world(
    _env: JNIEnv,
    _class: JClass,
) {
    println!("Hello World from Rust!");
}

/// Returns a "Hello World from Rust!" string that can be used in Java
/// JNI signature: ()Ljava/lang/String;
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustFFI_rust_1get_1hello_1string(
    mut env: JNIEnv,
    _class: JClass,
) -> jstring {
    let output = env.new_string("Hello World from Rust!")
        .expect("Couldn't create java string!");
    output.into_raw()
}

/// Prints a custom message passed from Java
/// JNI signature: (Ljava/lang/String;)V
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustFFI_rust_1print_1message(
    mut env: JNIEnv,
    _class: JClass,
    message: JString,
) {
    let message_str: String = env.get_string(&message)
        .expect("Couldn't get java string!")
        .into();
    
    println!("Rust received: {}", message_str);
}
