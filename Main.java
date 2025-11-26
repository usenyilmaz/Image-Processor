import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        boolean UseQuadTree = false;
        boolean UseEdgeDetection = false;
        boolean OutlineQuadTrees = false;
        String input = null;
        String output = null;

        for(int i = 0; i < args.length; i++){
            if(args[i].equals("-c")){UseQuadTree = true;}
            else if(args[i].equals("-e")){UseEdgeDetection = true;}
            else if(args[i].equals("-t")){OutlineQuadTrees = true;}
            else if(args[i].equals("-i")){input=args[i+1];}
            else if(args[i].equals("-o")){output = args[i + 1];}
        }
//        System.out.println(UseEdgeDetection);
//        System.out.println(UseQuadTree);
//        System.out.println(OutlineQuadTrees);
//        System.out.println(input);
//        System.out.println(output);



    }
}

/*
    You will receive an image le on the command line following the -i
    ag as your input: java Main -i test.ppm.
 -o <filename> indicates the name of the output le that your program should write to
 -c indicates that you should perform image compression by building quadtrees ----> boolean
 -e for edge detection -------> boolean
 -t indicates that output images should have the quadtree outlined ------> boolan

 For example:
java Main -c -i test.ppm -o out
 */