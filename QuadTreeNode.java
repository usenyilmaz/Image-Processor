public class QuadTreeNode <P>{
    P element;
    QuadTreeNode<P> parent;
    QuadTreeNode<P> northEast;
    QuadTreeNode<P> northWest;
    QuadTreeNode<P> southEast;
    QuadTreeNode<P> southWest;
    boolean state; // Durum: Düğümün bir yaprak olup olmadığını veya bölünmesi gerekip gerekmediğini gösteren bir bayrak

     public QuadTreeNode(P element, QuadTreeNode<P> parent) {
         this.element = element;
         this.parent = parent;
     }



}
//Çocuklar: Dört çocuk düğüm için referanslar (NE, NW, SW, SE).