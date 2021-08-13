const fs = require('fs');
const filePath = process.argv[2];
console.log('正在处理文件:' + filePath);
if (filePath) {
  if (fs.existsSync(filePath)) {
    try {
      fs.readFile(
        filePath,
        {
          encoding: 'utf8',
        },
        function (err, data) {
          if (err) {
            // check and handle err
            console.log(filePath, '文件读取失败');
          }
          console.log(filePath, '删除最后一行成功~');
          const datas = data.split('\n');
          const linesExceptFirst = datas.slice(0, datas.length - 1).join('\n');
          fs.writeFileSync(filePath, linesExceptFirst);
        },
      );
    } catch (error) {
      console.log(filePath, '文件读取失败');
      console.error(error);
    }
  } else {
    console.log(filePath, '文件不存在');
  }
}
