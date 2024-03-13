#include "libmewsic_engine_api.h"
#include "audio_api.h"

__attribute__ ((visibility ("default")))
audio_effect_library_t AUDIO_EFFECT_LIBRARY_INFO_SYM = {
    .tag = AUDIO_EFFECT_LIBRARY_TAG,
    .version = EFFECT_LIBRARY_API_VERSION,
    .name = "Mewsic Engine",
    .implementor = "Uninit",
    .create_effect = reinterpret_cast<int32_t(*)(const effect_uuid_t*,int32_t,int32_t,effect_handle_t*)>(MECreateEffect),
    .release_effect = reinterpret_cast<int32_t(*)(effect_handle_t)>(MEReleaseEffect),
    .get_descriptor = reinterpret_cast<int32_t(*)(const effect_uuid_t*,effect_descriptor_t*)>(MEGetDescriptor),
};
