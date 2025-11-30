public class QuadTree<T> {

    // Eklenecek düğümün maksimum kapasitesini (veya bölme derinliğini) temsil edebilir.
    // Basit bir Linked Quadtree örneği olduğu için bu değeri şimdilik 1 olarak tutuyoruz.
    private static final int MAX_CAPACITY = 1;

    // Ağacın kök düğümü
    private Node<T> root;

    /**
     * Düğümleri temsil eden iç içe (inner) ve statik (static) sınıf.
     * Bu, düğümlerin QuadTree sınıfına bağlı olmadan var olmasını sağlar.
     */
    public static class Node<T> {
        // Temsil edilen bölgenin sınırları (örneğin, bir dikdörtgen).
        // Basitlik için koordinatları tutmuyoruz, sadece yapısal bir örnek veriyoruz.
        // Gerçek bir Quadtree'de burada minX, minY, maxX, maxY gibi alanlar olur.

        // Düğümün depoladığı veri (Generic T tipinde)
        private T data;

        // Çocuk düğümler (KuzeyBatı, KuzeyDoğu, GüneyBatı, GüneyDoğu)
        // Quadtree'nin temel özelliği olan 4 çocuk bağlantısı
        private Node<T> northWest; // NW
        private Node<T> northEast; // NE
        private Node<T> southWest; // SW
        private Node<T> southEast; // SE

        // Yapıcı Metot
        public Node(T data) {
            this.data = data;
        }

        // --- Getter ve Setter Metotları ---

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public Node<T> getNorthWest() {
            return northWest;
        }

        public void setNorthWest(Node<T> northWest) {
            this.northWest = northWest;
        }

        public Node<T> getNorthEast() {
            return northEast;
        }

        public void setNorthEast(Node<T> northEast) {
            this.northEast = northEast;
        }

        public Node<T> getSouthWest() {
            return southWest;
        }

        public void setSouthWest(Node<T> southWest) {
            this.southWest = southWest;
        }

        public Node<T> getSouthEast() {
            return southEast;
        }

        public void setSouthEast(Node<T> southEast) {
            this.southEast = southEast;
        }

        /**
         * Düğümün yaprak olup olmadığını kontrol eder.
         */
        public boolean isLeaf() {
            return northWest == null && northEast == null && southWest == null && southEast == null;
        }
    }

    // --- QuadTree Sınıfı Metotları ---

    public QuadTree() {
        this.root = null;
    }

    public T getRoot() {
        return root.getData();
    }

    public boolean isEmpty(){
        return root == null;
    }

    public void Accept(T elemenent, T NE, T NW, T SE, T SW){
        Node<T> aux = new Node<>(elemenent);
        aux.setNorthEast(new Node<T> (NE));
        aux.setNorthWest(new Node<T>(NW));
        aux.setSouthEast(new Node<T>(SE));
        aux.setSouthWest(new Node<T>(SW));


    }

    /**
     * Ağaca kök düğüm olarak bir veri ekler (Ağaç boşsa).
     */
    public void insertRoot(T data) {
        if (this.root == null) {
            this.root = new Node<>(data);
            System.out.println("Kök düğüm eklendi: " + data);
        } else {
            System.out.println("Hata: Kök zaten mevcut. Başka bir işlem kullanın.");
        }
    }
    public void insertRoot(Node<T> node) {
        if (this.root == null) {
            this.root = node;
        }
    }



    /**
     * Belirtilen ebeveyn düğüme (parent) belirli bir yöne (örneğin NW) yeni bir düğüm ekler.
     * Bu metot, Quadtree'nin bağlı (linked) yapısını gösterir.
     * * @param parent Ebeveyn düğüm.
     * @param direction Ekleme yönü (1=NW, 2=NE, 3=SW, 4=SE).
     */
    public void insert(Node<T> parent, Node<T> newNode, int direction) {
        if (parent == null) {
            return;
        }

        switch (direction) {
            case 1: // North-West (KuzeyBatı)
                parent.setNorthWest(newNode);
                break;
            case 2: // North-East (KuzeyDoğu)
                parent.setNorthEast(newNode);
                break;
            case 3: // South-West (GüneyBatı)
                parent.setSouthWest(newNode);
                break;
            case 4: // South-East (GüneyDoğu)
                parent.setSouthEast(newNode);
                break;
            default:
                System.out.println("Hata: Geçersiz yön belirtildi.");
                return;
        }
    }

    /**
     * Ağacın içeriğini basitçe dolaşan (Pre-order) özyinelemeli metot.
     */
    public void traversePreOrder(Node<T> node) {
        if (node != null) {
            // Kökü ziyaret et
            System.out.print(node.getData() + (node.isLeaf() ? " (Yaprak) " : " -> "));

            // Özyinelemeli olarak çocukları ziyaret et
            traversePreOrder(node.getNorthWest());
            traversePreOrder(node.getNorthEast());
            traversePreOrder(node.getSouthWest());
            traversePreOrder(node.getSouthEast());
        }
    }
}