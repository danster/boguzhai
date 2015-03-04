#! /usr/bin/python
# -*- coding: utf-8 -*-

'''
    Created on Sep 9, 2014
    @author: Huang Maofeng
    '''
from PIL import Image   # could open an image file (.bmp,.jpg,.png,.gif)

############### GLOBAL VALUE #################
# 常见条形栏里的图标高度
base_icon_height = 50

# 账户主页条形栏左侧图标宽度
account_icon_width = 60

##############################################

def svg2png():
    # connt import cairo and rsvg
    
    import cairo
    import rsvg
    
    img =  cairo.ImageSurface(cairo.FORMAT_ARGB32, 640,480)
    ctx = cairo.Context(img)
    handler= rsvg.Handle(open('home_circle.svg'))
    # or, for in memory SVG data:
    # handler= rsvg.Handle(None, str(<svg data>))
    
    handler.render_cairo(ctx)
    img.write_to_png("home_circle.png")

def png_transfer(name, new_name=None, width=None, height=None, org_dir=None):
    
    # refer to : http://pillow.readthedocs.org/en/latest/reference/Image.html
    if org_dir is not None :
        url_dir = org_dir
    else :
        url_dir = "app/src/main/res/drawable/"
    new_url_dir = "app/src/main/res/drawable/"
    url = url_dir + name
    
    if new_name is None :
        new_url = new_url_dir + name
    else :
        new_url = new_url_dir + new_name

    image = Image.open(url)
    org_width, org_height = image.size

    new_width, new_height = (org_width, org_height)

    if width is not None and height is None :
        new_width  = width
        new_height = new_width*org_height/org_width

    if width is None and height is not None :
        new_height = height
        new_width = new_height*org_width/org_height

    if width is not None and height is not None :
        new_width, new_height = (width, height)

    image.thumbnail((new_width, new_height), Image.ANTIALIAS) # change pixels
    #image.resize((new_width, new_height), Image.ANTIALIAS) // not able on png
    image.save(new_url,"PNG")


def main_icon():
    tab_height = 30;
    
    png_transfer("main_tab_home.png", height=tab_height)
    png_transfer("main_tab_home_on.png", height=tab_height)
    png_transfer("main_tab_auction.png", height=tab_height)
    png_transfer("main_tab_auction_on.png", height=tab_height)
    png_transfer("main_tab_search.png", height=tab_height)
    png_transfer("main_tab_search_on.png", height=tab_height)
    png_transfer("main_tab_me.png", height=tab_height)
    png_transfer("main_tab_me_on.png", height=tab_height)

def search():
    png_transfer("search_auction.png","search_auction.png",height=150,org_dir="icon/")
    png_transfer("search_lot.png","search_lot.png",height=150,org_dir="icon/")


if __name__ == '__main__':
    main_icon()
    search()























