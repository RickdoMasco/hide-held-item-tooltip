const baseConfig = require('./run/node_modules/@rickdomasco/prettier-config');

const { jsxBracketSameLine, ...prettierConfig } = baseConfig;

module.exports = prettierConfig;
