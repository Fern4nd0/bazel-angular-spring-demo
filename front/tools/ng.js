#!/usr/bin/env node
process.env.CI = process.env.CI || "true";
process.env.NG_CLI_ANALYTICS = "false";
process.env.NG_CLI_ANALYTICS_SHARE = "false";
require('@angular/cli/bin/ng');
