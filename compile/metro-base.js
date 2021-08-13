const fs = require('fs');
const md5 = require('crypto-js/md5');

const buildType = process.argv[4];
const {name: appName} = require('../app.json');
const commonModulesFileName = `${__dirname}/common-modules-${buildType}.json`;
const commonModulesIndexMapFileName = `${__dirname}/common-modules-index-map-${buildType}.json`;
const pathM = require('path');
const pathSep = pathM.sep;
const base = process.cwd();

// 使用名称作为id
const commonAndroid = require(`${base}/compile/common-modules-android.json`);
const commonIOS = require(`${base}/compile/common-modules-ios.json`);
const commonModules = buildType === 'ios' ? commonIOS : commonAndroid;

// 使用数字作为id
const commonModulesIndexMap =
  buildType === 'ios'
    ? require(`${base}/compile/common-modules-index-map-ios.json`)
    : require(`${base}/compile/common-modules-index-map-android.json`);
// 针对id使用数字，随机生成
const randomNum = +`${+new Date()}`.slice(-9);
// 是否使用index作为表示
const moduleIdByIndex = true;

function clearFileInfo() {
  if (moduleIdByIndex) {
    fs.writeFileSync(commonModulesIndexMapFileName, '{}');
  } else {
    fs.writeFileSync(commonModulesFileName, '[]');
  }
}

function createModuleIdFactory() {
  let nextId = randomNum;
  const fileToIdMap = new Map();

  return path => {
    if (!moduleIdByIndex) {
      const name = getModuleIdByName(base, path);
      return name;
    }
    // 公共包的Id
    const relPath = pathM.relative(base, path);
    if (commonModulesIndexMap[relPath]) {
      return commonModulesIndexMap[relPath];
    }
    // 业务包的Id
    let id = fileToIdMap.get(path);
    if (typeof id !== 'number') {
      id = nextId + 1;
      nextId = nextId + 1;
      fileToIdMap.set(path, id);
    }
    return id;
  };
}

function processModuleFilter() {
  return module => {
    const {path} = module;
    const relPath = pathM.relative(base, path);
    if (
      path.indexOf('__prelude__') !== -1 ||
      path.indexOf('/node_modules/react-native/Libraries/polyfills') !== -1 ||
      path.indexOf('source-map') !== -1 ||
      path.indexOf('/node_modules/metro-runtime/src/polyfills/require.js') !==
        -1
    ) {
      return false;
    }
    // 使用name的情况
    if (!moduleIdByIndex) {
      if (commonModules.includes(relPath)) {
        return false;
      }
    } else {
      // 使用id的情况
      if (commonModulesIndexMap[relPath]) {
        return false;
      }
    }

    return true;
  };
}
/**
 * 公共包分包，id创建，如果是以id则从0开始，否则按照路径名称开始
 */
function createCommonModuleIdFactory() {
  let nextId = 0;
  const fileToIdMap = new Map();

  return path => {
    if (!moduleIdByIndex) {
      const name = getModuleIdByName(base, path);
      const relPath = pathM.relative(base, path);
      if (!commonModules.includes(relPath)) {
        // 记录路径
        commonModules.push(relPath);
        fs.writeFileSync(commonModulesFileName, JSON.stringify(commonModules));
      }
      return name;
    }
    let id = fileToIdMap.get(path);

    if (typeof id !== 'number') {
      id = nextId + 1;
      nextId = nextId + 1;
      fileToIdMap.set(path, id);
      const relPath = pathM.relative(base, path);
      if (!commonModulesIndexMap[relPath]) {
        // 记录路径和id的关系
        commonModulesIndexMap[relPath] = id;
        fs.writeFileSync(
          commonModulesIndexMapFileName,
          JSON.stringify(commonModulesIndexMap),
        );
      }
    }
    return id;
  };
}

/** 根据模块路径返回moduleId，优点是简单且确保唯一，缺点是无法使用rambundle打包方式*/
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
  name = name.replace('@', '_'); // 把path中的/换成下划线

  return name;
}

module.exports = {
  createModuleIdFactory,
  processModuleFilter,
  clearFileInfo,
  moduleIdByIndex,
  createCommonModuleIdFactory,
};
