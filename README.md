# minio-client
spring:
  minio:
    # Minio Host
    url: http://10.0.0.240:9000/
    # 您的应用的Minio桶名称
    bucket: test
    # Minio access key (用户名)
    access-key: minioadmin
    # Minio secret key (密码)
    secret-key: QWERTYUIOPASDFGHJKLZXCVBNM
    #水印及压缩配置
    watermark:
      #图片压缩率
      image-ratio: 0.1f
      #图片压缩比
      image-width: 50
      #水印文字内容
      text: http://zhskg.cn
      #水印透明度
      alpha: 0.1
      #水印文字配置
      #水印文字大小
      font-size: 36
      #文字字体
      font-name: PingFang SC Regular
      #水印文字颜色 通过红蓝绿配置
      color-red: 111
      color-blue: 111
      color-green: 111
      #水印之间的间隔
      x-move: 80
      y-move: 80


  ## 文件上传配置 (MultipartProperties)
  servlet:
    multipart:
      # 启用分段上传
      enabled: true
      # 当文件达到多少时进行磁盘写入
      file-size-threshold: 2KB
      # 单个文件的大小
      max-file-size: 5MB
      # 单个请求的大小
      max-request-size: 10MB
