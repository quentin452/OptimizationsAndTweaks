// TODO
use optimizationsandtweaks_shared::{init_panic_logging, log_native_line};
use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring, JNI_VERSION_1_6};
use std::os::raw::c_void;
#[no_mangle]
pub unsafe extern "system" fn JNI_OnLoad(_vm: *mut jni::sys::JavaVM, _reserved: *mut c_void) -> jint {
    // Init panic hook and any global state here.
    init_panic_logging();
    log_native_line("JNI_OnLoad: library loaded");

    // Return the JNI version we support.
    JNI_VERSION_1_6
}

#[no_mangle]
pub unsafe extern "system" fn JNI_OnUnload(_vm: *mut jni::sys::JavaVM, _reserved: *mut c_void) {
    log_native_line("JNI_OnUnload: library unloading");
}

/// Simple void function that logs a message. JNI signature: ()V
#[no_mangle]
pub extern "system" fn Java_com_example_native_NativeLib_rustHelloWorld(
    _env: JNIEnv,
    _class: JClass,
) {
    log_native_line("Hello from Rust (hello_world)");
}

/// Returns a Java string. JNI signature: ()Ljava/lang/String;
#[no_mangle]
pub extern "system" fn Java_com_example_native_NativeLib_getHelloString(
    mut env: JNIEnv,
    _class: JClass,
) -> jstring {
    let s = env
        .new_string("Hello from Rust!")
        .expect("Couldn't create java string");
    s.into_raw()
}

/// Receives a Java string and logs it. JNI signature: (Ljava/lang/String;)V
#[no_mangle]
pub extern "system" fn Java_com_example_native_NativeLib_printMessage(
    mut env: JNIEnv,
    _class: JClass,
    message: JString,
) {
    if let Ok(msg) = env.get_string(&message) {
        let msg_str: String = msg.into();
        log_native_line(&format!("Java->Rust message: {}", msg_str));
    } else {
        log_native_line("Java->Rust message: <invalid string>");
    }
}