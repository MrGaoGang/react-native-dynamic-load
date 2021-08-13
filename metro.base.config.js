const metroCfg = require('./compile/metro-base');
metroCfg.clearFileInfo();
module.exports = {
  serializer: {
    createModuleIdFactory: metroCfg.createCommonModuleIdFactory,
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
