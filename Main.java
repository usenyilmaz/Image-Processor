import java.awt.image.BufferedImage;
import java.io.IOException;

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
        ImageProcessor processor = new ImageProcessor(input,output);
        try{
            Image originalImage = processor.ReadImage();

            if(UseQuadTree){
                processor.GenerateCompressedImages(originalImage, output);
            }
            else if (UseEdgeDetection) { // -e bayrağı: Kenar Tespiti

            // Adım 1: Kenar Tespiti için Quadtree'yi inşa et (threshold ile)
            // Kenar tespiti için tek bir threshold bulmanız gerekecek.
            // processor.BuildEdgeDetectionTree(originalImage, EDGE_THRESHOLD);

            // Adım 2: Filtreyi sadece küçük düğümlere uygulayarak görüntüyü oluştur
            // Image resultImage = processor.ApplyEdgeDetection(originalImage);

            // Adım 3: Eğer -t varsa, Quadtree çerçevesini çiz
            if (OutlineQuadTrees) {
                // processor.DrawQuadtreeOutline(resultImage, processor.getQuadTree().getRoot());
            }

            // Adım 4: Tek bir çıktı dosyasına yaz
            // processor.WriteImage(resultImage, output);

        }


            else {
                // Eğer -c veya -e yoksa, varsayılan olarak ne yapılacağı (örneğin sadece okunan görüntüyü yaz)
                System.out.println("Hata: -c (sıkıştırma) veya -e (kenar tespiti) bayrağı belirtilmedi.");
                // processor.WriteImage(originalImage, output); // Sadece test amaçlı, okunanı yazar.
            }



        } catch(Exception e){
            e.printStackTrace();
        }





    }
}

