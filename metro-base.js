const {name: appName} = require('./app.json');
const pathM = require('path');
const pathSep = pathM.sep;
const base = process.cwd();
const md5 = require('crypto-js/md5');
function createModuleIdFactory() {
  return path => {
    const id = getModuleIdByName(base, path);
    return md5(id).toString();
  };
}

function getModuleIdByName(projectRootPath, path) {
  let name = '';
  if (
    path.indexOf(
      `node_modules${pathSep}react-native${pathSep}Libraries${pathSep}`,
    ) > 0
  ) {
    // 这里是react native 自带的库，因其一般不会改变路径，所以可直接截取最后的文件名称
    name = path.substr(path.lastIndexOf(pathSep) + 1);
  } else if (path.indexOf(`${projectRootPath + pathSep}node_modules`) === 0) {
    // 针对node_modules的，加上node_modules前缀
    /*
        这里是react native 自带库以外的其他库，因是绝对路径，带有设备信息，
        为了避免重复名称,可以保留node_modules直至结尾
        如node_modules/xxx.js 需要将设备信息截掉
      */
    name = path.substr(projectRootPath.length + 1);
  } else if (path.indexOf(`${projectRootPath}`) === 0) {
    // 针对本项目的，加上项目特定前缀
    name = appName + pathSep + path.substr(projectRootPath.length + 1);
  }
  name = name.replace('.js', '');
  name = name.replace('.png', '');
  const regExp =
    pathSep === '\\' ? new RegExp('\\\\', 'gm') : new RegExp(pathSep, 'gm');
  name = name.replace(regExp, '_'); // 把path中的/换成下划线
  return name;
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
