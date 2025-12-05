# Image Processor
Project by Ural Şenyılmaz. no: 241401014. 

# Features
The ImageProcessor supports the following operations, triggered by command-line flags:Lossy Compression (-c): Generates 8 different compressed versions of the input image based on achieving specific leaf-to-pixel ratios (compression levels).Edge Detection (-e): Applies a $3 \times 3$ Laplace convolution filter for edge detection only to the small, high-detail regions identified by the Quadtree structure. Larger, uniform regions are colored black.Quadtree Outlining (-t): Draws the bounding boxes of all leaf nodes in the generated Quadtree onto the output image, useful for debugging and visualizing the tree's structure.PPM Support: Handles image input and output in the P3 (ASCII) PPM format.

# Project Structure
The repository contains the following key classes:

Main.java: Handles command-line argument parsing and manages the application workflow based on input flags (-c, -e, -t).

ImageProcessor.java: Contains the main logic for reading/writing images, building the Quadtree, and executing compression/filtering algorithms.

Image.java: Represents a square region of the image, handles sub-image creation, and calculates mean color and mean squared error (MSE).

Pixel.java: Represents an RGB color value and manages data integrity (0-255 range).

QuadTree.java: Implements the generic linked Quadtree structure, including the Node class.


# How to Run the Program
The program expects a square input image whose dimensions are a power of 2. 

#Compilation
Make sure you open the project in an integrated terminal. Compile all Java source files using the command:
javac *.java

#Execution
Use the java Main command followed by the necessary flags. Note that only one of -c or -e should be used per execution.

**1) Compression mode**

**i) Basic Compression**
java Main -i kira.ppm -o compressed_out -c

**ii) Compression with Quadtree Outlines**
java Main -i kira.ppm -o compressed_framed -c -t
Output files: compressed_framed-1.ppm, ..., compressed_framed-8.

**2) Edge Detection Mode (Producing 1 Output File)**
Applies the Laplace filter based on a pre-determined Quadtree structure.
**i) Edge Detection Only**
java Main -i kira.ppm -o edge_result -e

**ii) Edge Detection with Quadtree Outlines**
java Main -i kira.ppm -o edge_framed -e -t
Output file: edge_framed.ppm

# Bugs and Limitations
**Input Image Requirements:** 
The program mandates that the input image must be square and its dimension must be a power of two (e.g., $256 \times 256$, $512 \times 512$). Non-compliant images will result in an error and termination.

**Fixed Threshold Search:**
In compression mode (-c), the iterative threshold search relies on a fixed number of iterations (MAX_ITERATIONS) and pre-set search bounds (globalMaxThreshold). For images with extremely high or low color variance, these bounds might need manual adjustment to accurately hit the target compression levels.

**Memory Efficiency:**
The Quadtree is built using linked nodes, and every node stores an Image object representing its region. For large images, this design is memory-intensive, as it duplicates pixel data across many nodes in the tree. An optimized version would store only the boundaries/coordinates within the nodes.

