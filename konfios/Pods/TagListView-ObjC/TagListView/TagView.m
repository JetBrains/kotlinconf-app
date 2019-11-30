//
//  TagView.m
//  TagObjc
//
//  Created by Javi Pulido on 16/7/15.
//  Copyright (c) 2015 Javi Pulido. All rights reserved.
//

#import "TagView.h"

@implementation TagView

@synthesize textColor =_textColor;
@synthesize textFont = _textFont;

- (instancetype) initWithTitle:(NSString *)title {
    self = [super init];
    if(self) {
        [self setTitle:title forState:UIControlStateNormal];
        CGSize intrinsicSize = [self intrinsicContentSize];
        self.frame = CGRectMake(0, 0, intrinsicSize.width, intrinsicSize.height);
    }
    return self;
}

- (CGSize) intrinsicContentSize {
    CGSize size = [self.titleLabel.text sizeWithAttributes:@{NSFontAttributeName: self.titleLabel.font}];
    
    size.height = self.titleLabel.font.pointSize + self.paddingY * 2;
    size.width += self.paddingX * 2;
    
    return size;
}

# pragma mark - Getters

- (UIColor *)textColor {
    if(!self.textColor) {
        self.textColor = [UIColor blackColor];
    }
    return self.textColor;
}

- (UIFont *)textFont {
    if(!self.textFont) {
        self.textFont = [UIFont systemFontOfSize:12];
    }
    return self.textFont;
}

# pragma mark - Setters

- (void)setCornerRadius:(CGFloat)cornerRadius {
    _cornerRadius = cornerRadius;
    self.layer.cornerRadius = cornerRadius;
    self.layer.masksToBounds = self.cornerRadius > 0;
}

- (void)setBorderWidth:(CGFloat)borderWidth {
    _borderWidth = borderWidth;
    self.layer.borderWidth = borderWidth;
}

- (void)setBorderColor:(UIColor *)borderColor {
    _borderColor = borderColor;
    self.layer.borderColor = borderColor.CGColor;
}

- (void)setTextColor:(UIColor *)textColor {
    _textColor = textColor;
    [self setTitleColor:textColor forState:UIControlStateNormal];
}

- (void)setPaddingY:(CGFloat)paddingY {
    _paddingY = paddingY;
    UIEdgeInsets insets = [self titleEdgeInsets];
    insets.top = paddingY;
    insets.bottom = paddingY;
    [self setTitleEdgeInsets:insets];
}

- (void)setPaddingX:(CGFloat)paddingX {
    _paddingX = paddingX;
    UIEdgeInsets insets = [self titleEdgeInsets];
    insets.left = paddingX;
    insets.right = paddingX;
    [self setTitleEdgeInsets:insets];
}

- (void)setTextFont:(UIFont *)textFont {
    _textFont = textFont;
    [self.titleLabel setFont:textFont];
}

@end
