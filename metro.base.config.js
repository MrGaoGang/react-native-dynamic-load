const metroCfg = require('./metro-base');

module.exports = {
  serializer: {
    createModuleIdFactory: metroCfg.createModuleIdFactory,
    processModuleFilter: metroCfg.processModuleFilter('BASE'),
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
