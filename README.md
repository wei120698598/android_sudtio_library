# android_sudtio_library#
[![](https://jitpack.io/v/wei120698598/android_sudtio_library.svg)](https://jitpack.io/#wei120698598/android_sudtio_library)

Please ensure that you are using the latest version by <br>
 [bintray checking here](https://bintray.com/bintray/jcenter)<br>
 [github checking here](https://github.com/wei120698598/android_sudtio_library)<br>
 [coding checking here](https://coding.net/u/wei120698598/p/android_sudtio_library/git)<br>
 [jitpack checking here](https://jitpack.io/#wei120698598/android_sudtio_library)

android_sudtio_library 里面主要集成了在Android App开发过程中常用的工具类、自定义view、联网、数据操作等工具，
这样在开发过个app的时候，可以有效避免浪费不必要的时间，以及去到处搜索常用的控件等，方便开发，节省时间，自己不必再造轮子。<br>
工具中的类或实现效果可以通过源代码进行稍加更改变成自己想要的。如果有更好的实现方法，多多提交。<br>
这个工具库中有一部分借鉴了各位大牛写的代码以及开源的库，在此对各位大牛表示感谢。也希望各位大神多提意见，贡献自己的代码，大家共同进步。<br>

这个库主要针对以Android Studio为主要开发工具的项目，使用了大量的新控件，新功能，新效果等，如果构建版本发生冲突，可以下载源代码对构建版本进行更改。

由于此库包含了大量常用的工具，如果只需要其中的某个工具可以下载源码，进行剥离。每个模块都使用了单独的包名，所以剥离也较容易

----------------------------

as_lib_CustomUtils 主要包含了常用的工具类，例如TextUtils，日志打印，手机信息获取等

as_lib_DatabaseUtils 主要包含了对数据库操作常用的工具类，这里面主要对开源的库进行了二次封装，例如对GreenDao的封装

as_lib_ImageUtils 主要包含了对图片操作常用的工具类，例如对图片的压缩、上传、处理等等

as_lib_NetWorkUtils 主要包含了与联网相关的工具类，里面有自己封装的以及对开源的库二次封装，例如对volley的二次封装

as_lib_CustomView 主要包含了常用的自定义View，例如圆形图片，图片选择，自定义的ListView等等

----------------------------



Add as_lib_CustomUtils to your project
----------------------------
```gradle
compile 'com.wei.utils:as_lib_CustomUtils:1.0.2'
```


Add as_lib_DatabaseUtils to your project
----------------------------

```gradle
compile 'com.wei.db:as_lib_DatebaseUtils:1.0.2'
```


Add as_lib_ImageUtils to your project
----------------------------

```gradle
compile 'com.wei.image:as_lib_ImageUtils:1.0.2'
```


Add as_lib_NetWorkUtils to your project
----------------------------

```gradle
compile 'com.wei.net:as_lib_NetWorkUtils:1.0.2'
```


Add as_lib_CustomView to your project
----------------------------

```gradle
compile 'com.wei.view:as_lib_CustomView:1.0.3'
```