# TagListView-ObjC

[![Version](https://img.shields.io/cocoapods/v/TagListView-ObjC.svg?style=flat)](http://cocoapods.org/pods/TagListView-ObjC)
[![License](https://img.shields.io/cocoapods/l/TagListView-ObjC.svg?style=flat)](http://cocoapods.org/pods/TagListView-ObjC)

Simple but highly customizable iOS tag list view, in Objective-C. The APIs are *not* stable before 1.0. Use it if you don't want to use the Swift version.

Supports Storyboard, Auto Layout, and @IBDesignable.

<img alt="Screenshot" src="Screenshots/Screenshot.png" width="310">

This project is a port to ObjC from XHacker's [TagListView](https://github.com/xhacker/TagListView) library, I tried to remain as compatible as possible. This README itself (and the images/screenshots) are from XHacker's too.

My motivation to do this: At work, I had an ObjC-written project, and want to use that library. I didn't like at all that my app increases 3MB on size (because Swift libs) for only a few code files. Using this version, the increase in size is very tiny (<5kb).

## Usage

The most convinient way is to use Storyboard, where you can set the attributes right in the Interface Builder. With [@IBDesignable](http://nshipster.com/ibinspectable-ibdesignable/), you can see the preview in real time.

<img alt="Interface Builder" src="Screenshots/Interface Builder.png" width="566">

You can add tag to the tag list view, or set custom font through code:

```Objective-C
tagListView.textFont = [UIFont systemFontOfSize:24];

[tagListView addTag:@"meow"];

[tagListView removeTag:@"meow"]; // all tags with title “meow” will be removed
[tagListView removeAllTags];
```
<!-- TODO delegate -->

You can also customize a particular tag:

```Objective-C
TagView *tagView = [tagListView addTag:@"blue"];
tagView.tagBackgroundColor = [UIColor blueColor];
```

Be aware that if you update a property (e.g. `tagBackgroundColor`) for a `TagListView`, all the inner `TagView`s will be updated.

## Installation

Turns out that installation via CocoaPods may be buggy. If you can't make it work from Interface Builder, please check this [Stack Overflow answer](http://stackoverflow.com/questions/26567650/live-rendering-a-custom-component-using-ib-designable-from-a-pod-dependency/28108248#28108248). Using it without CocoaPods seems to work perfectly, though.

TagListView-ObjC is available through [CocoaPods](http://cocoapods.org). To install
it, simply add the following line to your Podfile:

```ruby
pod "TagListView-ObjC"
```

Instead, if you don't want to use CocoaPods, you can simply checkout the sample project. Library files are in the **"TagListView/"** folder, simply drag it into your project.

## Contribution

Pull requests are welcome! If you want to do something big, please open an issue to let me know first.

## License

TagListView-ObjC is available under the MIT license. See the LICENSE file for more info.
