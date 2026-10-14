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

### 功能特性（News）：
* pack line:

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_line.png)

* pack max size:

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_max_size.png)

* pack polygon:

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_polygon.png)

* pack non:

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_non.png)

* unpack polygon:

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_unpack_polygon.png)


<a id="about-the-support"></a>

## 支持说明（About The Support）：
* jpg, png, etc.
* json
* psd （待支持）

支持透明通道的png图片，用于存储图片路径，以及记录坐标尺寸的json文件。psd或其他打包软件文件待支持。

### 信息存储 （JSON）：

基础格式：

```json
{
    "0":{
        "name":"name1",
        "bounds":{
            "x":0,
            "y":0,
            "width":8,
            "height":8
        },
        "trims":{
            "left":2,
            "right":6,
            "top":2,
            "bottom":6
        }
    },
    "1":{
        "name":"name2",
        "bounds":{
            "x":0,
            "y":10,
            "width":8,
            "height":8
        },
        "trims":{
            "left":2,
            "right":6,
            "top":2,
            "bottom":6
        }
    }
}
```

或（旧版）

```json
{
    "0":{
        "name":"name1",
        "x":0,
        "y":0,
        "width":8,
        "height":8,
        "left":2,
        "right":6,
        "top":2,
        "bottom":6
    },
    "1":{
        "name":"name2",
        "x":0,
        "y":10,
        "width":8,
        "height":8,
        "left":2,
        "right":6,
        "top":2,
        "bottom":6
    }
}
```

现存储格式均为第一种，读取时也支持第二种。


<a id="about-the-item"></a>

## 参数说明（About The Item）：
* int x （x坐标）
* int y （y坐标）
* int width （图片宽度）
    * -1（max width）
    * 0 （keep width）
* int height （图片高度）
    * -1 （max height）
    * 0 （keep heght）
* int lineSize （行数）
    * -1, 0 （max line）
* int rowSize （列数）
    * -1, 0 （max row）
* int lineWidth （行间隔）
* int rowHeight （列间隔）
* int directionX （横向顺序）
    * -1 （从右到左）
    * 1 （从左到右）
* int directionY （纵向顺序）
    * -1 （从下到上）
    * 1 （从上到下）
* boolean isPackVertical （排列）
    * true （垂直排列）
    * false （水平排列）
* int packLevel （紧凑级别）
    * -1 （不处理）
    * 0 （普通）
    * 1 （极致）

keep size是指保持原有尺寸，max size是指所有图片文件最大尺寸，trim则是清除透明边框后最大有效像素尺寸，line size小于0时默认打包为1行，row size同样小于0时默认按图片数打包正方形图集。

### 举例（图中红框为图块轮廓，打包时不包含）：

```java
public static int Keep_Size = 0;
public static int Max_Size = -1;
```

1. keep size :

```java
packKeepSize(file, false);
//or
pack(file, 0, 0, 0, 0, -1, -1, 0, 0, false);
//or
pack(file, 0, 0, 0, 0, 3, 3, 0, 0, false);
```

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_keep_3x3.png)

2. keep size + trim :

```java
packKeepSize(file, true);
//or
pack(file, 0, 0, 0, 0, -1, -1, 0, 0, true);
//or
pack(file, 0, 0, 0, 0, 3, 3, 0, 0, true);
```

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_keep_3x3_trim.png)

3. max size :

```java
packMaxSize(file, false);
//or
pack(file, 0, 0, -1, -1, -1, -1, 0, 0, false);
//or
pack(file, 0, 0, -1, -1, 3, 3, 0, 0, false);
```

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_max_3x3.png)

4. max size + trim :

```java
packMaxSize(file, true);
//or
pack(file, 0, 0, -1, -1, -1, -1, 0, 0, true);
//or
pack(file, 0, 0, -1, -1, 3, 3, 0, 0, true);
```

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_max_3x3_trim.png)

5. pack polygon :

* 5.1 packPolygon :
* 5.2 packPolygonNon :

```java
packPolygon(file, false);
```

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_polygon_1.png)

```java
packPolygonNon(file, false);
```

以下情况适合packPolygonNon紧凑打包：

1.镂空图块：

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_polygon_non.png)

2.多边形图块：

(1) 不处理（packLevel -1）:

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_polygon_non_1.png)

(2) 普通（packLevel 0）:

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_polygon_non_2.png)

(3) 极致（packLevel 1）:

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_polygon_non_3.png)

pack功能可按参数要求打包图片为图集png。

packPolygon和packPolygonNon属于图块紧凑打包，前者按尺寸按顺序紧凑打包，后者查找像素透明空间紧凑打包。

打包功能附带生成JSON文件来存储图片的信息，例如坐标、尺寸等等，方便跨平台对png图集进行解包操作。

6. unpack :

* 6.1 unpack :
* 6.2 unpackNon :

```java
unpack(file, propFile, false);
```

unpack功能适用于pack和packPolygon产生的图集png解包（仍旧可用，但已被unpackNon替代）。

```java
unpackNon(file, propFile, false);
```

unpackNon功能适用于pack，packPolygon和packPolygonNon产生的图集png解包。

7. setting :
* 7.1 setPackVertical
* 7.2 setPackDirection
* 7.3 setPackLevel

```java
PixelPacker packer = new PixelPacker();
packer.setPackVertical(PixelSheet.Is_Pack_Horizontal);
packer.setPackDirection(PixelSheet.Direction_Right, PixelSheet.Direction_Left);
packer.setPackLevel(PixelSheet.Pack_Level_Extreme);
packer.setPackSize(256, 256);
packer.packPolygonNon(packImageFile, false);
```

![image](https://github.com/neverever1533/PixelPacker/blob/main/sheets_pack_polygon_non_4.png)

如图，设置横向setPackVertical(false)，以从左到右和从下到上的顺序，画布设置256*256像素，极致打包后的结果。

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
    * setPackDirection
    * setPackVertical
    * setPackLevel
    * setPackSize
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
        * pack polygon non
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
            * unpack
            * unpack non

打包或解包所有的方法。

<p align="right">(<a href="#method">回到方法（back to method）</a>)</p>

<p align="right">(<a href="#contents">回到目录（back to contents）</a>)</p>


<a id="about-the-method"></a>

## 方法说明 （About The Method）：


<a id="pack"></a>

#### 1.打包 （Pack）：

* setPackDirection

```java
public void setPackDirection(int directionX, int directionY) {}
```

排序方向默认向下和向右，directionX小于0，则在x坐标方向上以从右到左的顺序排序，反之则从左到右排序，directionY同理大于0则从上到下的顺序排列。

* setPackVertical

```java
public void setPackVertical(boolean isPackVertical) {}
```

打包图集顺序默认为纵向，isPackVertical为false则横向排列打包。

以上暂时packPolygon和packPolygonNon支持。

* setPackLevel

```java
public void setPackLevel(int packLevel) {}
```

使用packPolygonNon()时，setPackLevel()设置级别：

* 级别-1：
不处理待写入图片（默认）；

* 级别0：
检测图片透明边界，保持图片之间间隔1像素单位的透明边界（上下左右+4个角落）；

* 级别1：
如上，但省去四个角落的间隔。

---
tips：功能实现优先，性能优化待候。（暂时）

所以使用紧凑打包时，请限制画布尺寸为1024*1024以内，以及限制源图片的尺寸最大为256像素左右。（仅packPolygon和packPolygonNon受影响，使用带有json属性文件的pack(imageFile, propFile, isTrim)打包时不受尺寸影响。）

---

* setPackSize

```java
public void setPackSize(int width, int height){}
```

设置canvas画布尺寸。

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

packKeepSize(file, isTrim)和packMaxSize(file, isTrim)中，默认行列均为-1，行列间隔均为0，统一尺寸为所有图片中的最大尺寸，按图片数量自动计算行列，最终打包为正方形图集。

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

##### 4.1 混合打包（PackPolygon）

* pack polygon

```java
public void packPolygon(File file, boolean isTrim) {}
```

扫描文件夹，以图片次序，填充图块后打包为图集。

##### 4.2 混合打包（PackPolygonNon）

* pack polygon non

```java
public void packPolygonNon(File file, boolean isTrim) {}
```

扫描文件夹，以图片次序，画布检测透明空间，填充图块后打包为图集。（适用于非封闭、不连贯、或异形图块紧密打包。）


<a id="pack-properties"></a>

#### 5. 打包集 （Pack Properties）：

* pack properties

```java
public void pack(File imageFile, File propFile, boolean isTrim) {}
```

```java
public void pack(File imageFile, Properties properties, boolean isTrim) {}
```

扫描文件夹（若配置文件内有路径，第一项可设为null），按照配置信息打包图片为图集，propFile可选json或者xml文件。

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

读取图片文件，解包图片中的非透明部分的像素块，然后分割并存储为多个图片。（本功能代码暂不开源，若有需求可以向我申请使用，务必提供原图的版权拥有证明！）


<a id="unpack-properties"></a>

#### 8. 解包集 （Unpack Properties）：

* unpack properties

```java
public void unpack(File imageFile, File propFile, boolean isTrim) {}
```

```java
public void unpackNon(File imageFile, File propFile, boolean isTrim) {}
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