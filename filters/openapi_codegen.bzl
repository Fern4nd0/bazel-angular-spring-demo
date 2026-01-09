def _format_additional_properties(props):
    if props == None:
        return ""
    pairs = []
    for key, value in props.items():
        if type(value) == type(True):
            value_str = "true" if value else "false"
        else:
            value_str = str(value)
        pairs.append(key + "=" + value_str)
    return ",".join(pairs)

def openapi_generated_srcjar(
        name,
        spec,
        api_package,
        model_package,
        additional_properties = None,
        generator_name = "spring",
        visibility = None):
    additional_props = _format_additional_properties(additional_properties)
    cmd = """
      set -euo pipefail
      tmpdir=$$(mktemp -d)
      java -jar $(location @maven//:org_openapitools_openapi_generator_cli) generate \\
        -i $(location {spec}) \\
        -g {generator_name} \\
        -o $$tmpdir \\
        --global-property=apis,models \\
        --api-package {api_package} \\
        --model-package {model_package} \\
        --additional-properties {additional_props}
      python3 - "$@" "$$tmpdir/src/main/java" <<'PY'
import os
import sys
import zipfile

out = sys.argv[1]
root = sys.argv[2]

with zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as zf:
    for base, _, files in os.walk(root):
        for filename in files:
            path = os.path.join(base, filename)
            rel = os.path.relpath(path, root)
            zf.write(path, rel)
PY
    """.format(
        spec = spec,
        generator_name = generator_name,
        api_package = api_package,
        model_package = model_package,
        additional_props = additional_props,
    )

    native.genrule(
        name = name,
        srcs = [spec],
        outs = [name + ".srcjar"],
        tools = [
            "@maven//:org_openapitools_openapi_generator_cli",
        ],
        cmd = cmd,
        visibility = visibility,
    )
