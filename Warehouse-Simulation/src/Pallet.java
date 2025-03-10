public class Pallet extends Equipment{
    public Pallet(String quality){
        super(quality);
    }

    public void Break(){
        System.out.println("The pallet has broken!");
    }
}
