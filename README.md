# PixelPacker

Pack and Unpack images for the Sprite sheets.


<a id="contents"></a>

<details>
  <summary>目录（table of contents）</summary>
  <ol>
    <li>
      <a href="#project">项目说明（About The Project）</a>
    </li>
    <li><a href="#about-the-support">支持说明（About The Support）</a></li>
    <li><a href="#about-the-item">参数说明（About The Item）</a></li>
    <li>
      <a href="#method">方法 （Method）</a>
      <ul>
        <li><a href="#pack-all">打包 （Pack）</a></li>
        <li><a href="#unpack-all">解包 （Unpack）</a></li>
      </ul>
    </li>
    <li><a href="#about-the-method">方法说明 （About The Methods）</a></li>
    <li><a href="#license">许可 （License）</a></li>
  </ol>
</details>


<a id="project"></a>

## 项目说明（About The Project）：

本工程（Pixel Packer）适用于游戏图像资源打包，包含精灵图帧（sprite），瓦片地图（tilemap），UI图片资源集等等制作。

![image](https://github.com/neverever1533/PixelPacker/blob/main/PixelPacker_Info.png)


<a id="about-the-support"></a>

## 支持说明（About The Support）：
* jpg, png, etc.
* json
* psd （待支持）

支持透明通道的png图片，用于存储图片路径，以及记录坐标尺寸的json文件。psd或其他打包软件文件待支持。

<a id="about-the-item"></a>

## 参数说明（About The Item）：
* int x （x坐标）
* int y （y坐标）
* int width （图片宽度）
    * -1（max width）
    * 0 （keep width）
* int height （图片高度）
    * -1 （max width）
    * 0 （keep heght）
* int lineSize （行数）
    * -1, 0 （max line）
* int rowSize （列数）
    * -1, 0 （max row）
* int lineWidth （行间隔）
* int rowHeight （列间隔）

keep size是指保持原有尺寸，max size是指所有图片文件最大尺寸，trim则是清除透明边框后最大有效像素尺寸，line size小于0时默认打包为1行，row size同样小于0时默认按图片数打包正方形图集。

### 举例：
```java
public static int Keep_Size = 0;
public static int Max_Size = -1;
```
1. keep size + max size :

```java
pack(file, 0, 0, 0, 0, -1, -1, 0, 0, false);
```

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_keep_3x3.png)

2. keep size + max size + trim :

```java
pack(file, 0, 0, 0, 0, -1, -1, 0, 0, true);
```

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_keep_3x3_trim.png)

3. max size + max size :

```java
packMaxSize(file, false);
// or
pack(file, 0, 0, -1, -1, -1, -1, 0, 0, false);
```

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_max_3x3.png)

4. max size + max size + trim :

```java
packMaxSize(file, true);
// or
pack(file, 0, 0, -1, -1, -1, -1, 0, 0, true);
```

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_max_3x3_trim.png)

5. pack polygon :
```java
packPolygon(file, false);
```
![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_polygon.png)


<a id="method"></a>

## 方法 （Method）：

* <a href="#pack-all">打包（Pack）：</a>
    * pack
    * pack line
    * pack row
    * pack polygon
    * pack properties
* <a href="#unpack-all">解包（Unpack）：</a>
    * unpack
    * unpack line
    * unpack row
    * unpack polygon
    * unpack properties


<a id="pack-all"></a>

### 打包 （Pack）：

* <a href="#pack">打包 （Pack）：</a>
    * pack
    * pack keep size
    * pack max size
* <a href="#pack-line">打包行 （Pack Line）：</a>
    * pack line keep size
    * pack line max size
* <a href="#pack-row">打包列 （Pack Row）：</a>
    * pack row keep size
    * pack row keep size
* <a href="#pack-polygon">打包多边形 （Pack Polygon）：</a>
    * pack polygon
* <a href="#pack-properties">打包集 （Pack Properties）：</a>
    * pack properties


<a id="unpack-all"></a>

### 解包 （Unpack）：

* <a href="#unpack">解包 （Unpack）：</a>
    * unpack
* <a href="#unpack-polygon">解包多边形 （Unpack Polygon）：</a>
    * unpack polygon
* <a href="#unpack-properties">解包集 （unpack properties）</a>
    * unpack properties

打包或解包所有的方法。

<p align="right">(<a href="#method">回到方法（back to method）</a>)</p>

<p align="right">(<a href="#contents">回到目录（back to contents）</a>)</p>


<a id="about-the-method"></a>

## 方法说明 （About The Method）：


<a id="pack"></a>

#### 1.打包 （Pack）：

* pack
```java
public void pack(File file, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {}
```

* pack keep size
```java
public void packKeepSize(File file, boolean isTrim) {}
```

```java
public void packKeepSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {}
```

* pack max size
```java
public void packMaxSize(File file, boolean isTrim) {}
```

```java
public void packMaxSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {}
```

扫描文件夹（若选择文件，则扫描文件所在文件夹，pack的所有方法类同），将多个图片按照参数打包为指定行数和列数的图集。


<a id="pack-line"></a>

#### 2. 打包行 （Pack Line）：

* pack line keep size
```java
public void packLineKeepSize(File file, boolean isTrim) {}
```

```java
public void packLineKeepSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {}
```

* pack line max size
```java
public void packLineMaxSize(File file, boolean isTrim) {}
```

```java
public void packLineMaxSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {}
```

扫描文件夹，默认打包为一行图集。


<a id="pack-row"></a>

#### 3. 打包列 （Pack Row）：

* pack row keep size
```java
public void packRowKeepSize(File file, boolean isTrim) {}
```

```java
public void packRowKeepSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {}
```

* pack row max size
```java
public void packRowMaxSize(File file, boolean isTrim) {}
```

```java
public void packRowMaxSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {}
```

扫描文件夹，默认打包为一列图集。


<a id="pack-polygon"></a>

#### 4. 打包多边形 （Pack Polygon）：

* pack polygon
```java
public void packPolygon(File file, boolean isTrim) {}
```

扫描文件夹，不按照坐标并以图片次序打包为图集。


<a id="pack-properties"></a>

#### 5. 打包集 （Pack Properties）：

* pack properties
```java
public void pack(File imageFile, File propFile, boolean isTrim) {}
```

```java
public void pack(File imageFile, Properties properties, boolean isTrim) {}
```

扫描文件夹（若配置文件内有路径，第一项可设为null），按照配置信息打包图片为图集。

<p align="right">(<a href="#pack-all">回到打包 （back to pack）</a>)</p>

<p align="right">(<a href="#method">回到方法 （back to method）</a>)</p>


<a id="unpack"></a>

#### 6.解包 （Unpack）：

* unpack
```java
public void unpack(File file, int x, int y, int width, int height, boolean isTrim) {}
```

```java
public void unpack(File file, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {}
```

读取图片文件，按照坐标解包图片中的所有，或指定行数和列数的像素块（指定宽高），然后分割并存储为多个图片。


<a id="unpack-polygon"></a>

#### 7. 解包多边形 （Unpack Polygon）：

* unpack polygon
```java
public void unpackPolygon(File file, boolean isTrim) {}
```

```java
public void unpackPolygon(File file, int width, int height, boolean isTrim) {}
```

读取图片文件，解包图片中的非透明部分的像素块，然后分割并存储为多个图片。（已限制使用）


<a id="unpack-properties"></a>

#### 8. 解包集 （Unpack Properties）：

* unpack properties
```java
public void unpack(File imageFile, File propFile, boolean isTrim) {}
```

```java
public void unpack(File imageFile, Properties properties, boolean isTrim) {}
```

读取图片文件，按照配置信息解包图片，然后分割并存储为多个图片。

<p align="right">(<a href="#unpack-all">回到解包 （back to unpack）</a>)</p>

<p align="right">(<a href="#method">回到方法（back to method）</a>)</p>

<p align="right">(<a href="#contents">回到目录 （back to contents）</a>)</p>


<a id="license"></a>

## 许可（License）：

------------------

License :
 [Apache License (Version 2.0)](http://www.apache.org/licenses/)

------------------