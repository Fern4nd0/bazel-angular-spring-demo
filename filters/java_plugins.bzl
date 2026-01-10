def lombok_plugin(name = "lombok_plugin"):
    native.java_plugin(
        name = name,
        processor_class = "lombok.launch.AnnotationProcessorHider$AnnotationProcessor",
        deps = ["@maven//:org_projectlombok_lombok"],
    )
    return ":" + name

def mapstruct_plugin(name = "mapstruct_plugin"):
    native.java_plugin(
        name = name,
        processor_class = "org.mapstruct.ap.MappingProcessor",
        deps = [
            "@maven//:org_mapstruct_mapstruct_processor",
            "@maven//:org_projectlombok_lombok_mapstruct_binding",
        ],
    )
    return ":" + name
