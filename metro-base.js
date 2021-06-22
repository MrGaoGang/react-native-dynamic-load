function createModuleIdFactory() {
  const fileToIdMap = new Map();
  let nextId = 0;
  return path => {
    let id = fileToIdMap.get(path);
    if (typeof id !== 'number') {
      id = nextId++;
      fileToIdMap.set(path, id);
    }
    return id;
  };
}

function processModuleFilter(type) {
  return module => {
    if (type === 'ALL') {
      return true;
    } else if (type === 'BASE') {
      const projectName = __dirname.substr(__dirname.lastIndexOf('/') + 1);
      if (module.path.indexOf('__prelude__') !== -1) {
        return true;
      }
      if (module.path.indexOf(`${projectName}/node_modules`) !== -1) {
        return true;
      } else {
        return false;
      }
    } else if (type === 'BUSINESS') {
      const projectName = __dirname.substr(__dirname.lastIndexOf('/') + 1);
      if (module.path.indexOf('__prelude__') !== -1) {
        return false;
      }
      if (module.path.indexOf(`${projectName}/node_modules`) !== -1) {
        return false;
      } else {
        return true;
      }
    }
  };
}

module.exports = {
  createModuleIdFactory,
  processModuleFilter,
};
