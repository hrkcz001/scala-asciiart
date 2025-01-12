# BI-OOP: ASCII Art 

author: morozan1

supported arguments are:
- --image <path to the image file> (only one image can be specified, jpeg and png are supported)
- --generated-image (can be specified instead of --image)
- --output-file <path to the output file> (any number of unique output files can be specified)
- --output-console (can be specified only once)
- --rotate <angle> (will rotate image <angle> / 90 times, though any angle is supported)
- --scale <scale> (any float or integer, e.g. 0.0625 is image 16 times smaller)
- --invert (inverts the image)
- --table <name> (name of the table to use, "default", "extended" and "nonlinear" are supported, only one table can be specified)
- --custom-table <table> (custom table to use, string starting with character representing black color, will be linear if possible, e.g. "#Xx. ")

several filters can be applied in one run, e.g. --rotate 90 --rotate 90 --rotate 90 --rotate 90 will rotate the image 360 degrees
or --scale 0.5 --scale 0.5 will scale the image 4 times smaller

scale filters will be skipped if you try to generate 0 times smaller image

filters are optional
if no conversion table is specified, default table will be used
at least one output method and one image must be specified

bird.jpg and bear.png are provided as example images

example cli input:
--image bird.jpg --output-file result.txt --output-console --scale 0.0625 --table extended

# ASCII Art

[![pipeline status](https://gitlab.fit.cvut.cz/BI-OOP/B241/asciiart/badges/master/pipeline.svg)](https://gitlab.fit.cvut.cz/BI-OOP/B241/asciiart)

The idea of this project is to load images, translate them into ASCII ART images, optionally apply filters, and save them. (https://courses.fit.cvut.cz/BI-OOP/projects/ASCII-art.html)

## How to do it

1. **Make your repository private**
2. **Read [the instructions](https://courses.fit.cvut.cz/BI-OOP/projects/ASCII-art.html)**
3. Play [lofi hip hop radio](https://www.youtube.com/watch?v=dQw4w9WgXcQ&pp=ygUJcmljayByb2xs)
4. [???](https://www.youtube.com/watch?v=dQw4w9WgXcQ&pp=ygUJcmljayByb2xs)
5. Profit
