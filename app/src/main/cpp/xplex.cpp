// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("xplex");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("xplex")
//      }
//    }

#include "hooker.h"
#include "logger.h"

#include <dlfcn.h>
#include <sys/stat.h>

static HookFunType hook_func = nullptr;

void on_library_loaded(const char *name, void *handle)  {
    LOGI("Opening: %s", name);
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
NativeOnModuleLoaded native_init(const NativeAPIEntries *entries) {
    hook_func = entries->hook_func;
    // system hooks
    //hook_func((void*) fopen, (void*) fake_fopen, (void**) &backup_fopen);
    return on_library_loaded;
}