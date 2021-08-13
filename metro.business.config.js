const {
  createModuleIdFactory,
  processModuleFilter,
} = require('./compile/metro-base');

module.exports = {
  serializer: {
    createModuleIdFactory: createModuleIdFactory,
    processModuleFilter: processModuleFilter(),
  },
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: true,
      },
    }),
  },
};
