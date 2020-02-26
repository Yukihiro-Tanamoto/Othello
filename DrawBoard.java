/**
 * @version 0.02 7 Dec. 2015
 * @author Shogo Matsui
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
class DBPanel extends JPanel implements  MouseListener, ActionListener{
  final int r = 20;  // 石の半径
  final int dx = 50; // ボードのマスの間隔x
  final int dy = 50; // ボードのマスの間隔y
  int count =0; //ボード表示のカウント

  final Color Bc = new Color(0, 200, 0); // ボードの背景色
  final Color Lc = new Color(0, 0, 0);   // マスの枠線の色
  final Color Ccb = new Color(255, 255, 255); // 白石の色
  final Color Ccw = new Color(0, 0, 0); // 黒石の色

  // static public boolean vflag = false;       //verboseフラグ
  // boolean wflag = false;     // 白番プレーヤ（デフォルトは人間）
  // boolean bflag = false;     // 黒番プレーヤ（デフォルトは人間）

  // Board[] barr = new Board[120];  // Board を保存する配列を用意
  // int barr_pt = 0;                // 配列のインデックス用の変数

  // KeyIn kin = new KeyIn(); // Key入力用オブジェクト
  // Board bd  = new Board(); // Boardデータ用
  // Point newpt;             // Point一次保存用
  Point point;
  // 描くべきボードの配列
  int[][] board = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 0, 0, 0,-1, 1, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 1,-1, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} };

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    int w = 0, b = 0;
    for(int i = 1; i <= 8; i++) {
      for(int j = 1; j <= 8; j++) {
        int cx = dx * i;          // 描く図形の中心座標x
        int cy = dy * j;          // 描く図形の中心座標y
        int color = board[i][j];  // 描くデータ
        g.setColor(Bc);  // Bcで塗りつぶした正方形を描く
        g.fillRect(cx - dx/2, cy - dy/2, dx, dy);
        g.setColor(Lc);  // Lcで正方形を描く
        g.drawRect(cx - dx/2, cy - dy/2, dx, dy);
        if(color != 0) { // データが0でなければ円を描く
          g.setColor(color == 1? Ccw: Ccb);
          g.fillOval(cx - r, cy - r, 2 * r, 2 * r);
          if(color == 1)
            w++;
          else
            b++;
        }
      }
    }
    DrawBoard.wscore.setText("White:" + ( w/10 ) + ( w%10 ));
    DrawBoard.bscore.setText("Black:" + ( b/10 ) + ( b%10 ));

    
  }

  public void actionPerformed(ActionEvent evt) {
    String position = evt.getActionCommand();

    int i = Character.digit(position.charAt(0), 10);
    int j = Character.digit(position.charAt(1), 10);

    System.out.println("i=" + i + " j=" + j);
  }

  public void mouseClicked(MouseEvent evt) {
    int x = evt.getX();
    int y = evt.getY();
    int i = (x + dx/2) / dx;
    int j = (y + dy/2) / dy;
    point = new Point(i, j);
    
    //System.out.println(10*i+j);
    System.out.println("i=" + i + " j=" + j);
    // if(board[i][j]==0){
    //   board[i][j]=1; 
    //   repaint();
      
    // }
    // else if(board[i][j]==1){
    //   board[i][j]=-1; 
    //   repaint();
      
    // }
    // else{
    //   board[i][j]=0; 
    //   repaint();
      
    // }    
    
  }
  public void mousePressed(MouseEvent evt) {}
  public void mouseExited(MouseEvent evt) {}
  public void mouseReleased(MouseEvent evt) {}
  public void mouseEntered(MouseEvent evt) {}
}

class WinCloser extends WindowAdapter {
  public void windowClosing(WindowEvent e) {
    System.exit(0);
  }
}

public class DrawBoard {
  static JLabel wscore = new JLabel();
  static JLabel bscore = new JLabel();
  static public boolean vflag = false;       //verboseフラグ
  public static void main(String[] args) {
    JFrame frame = new JFrame();

  boolean wflag = false;     // 白番プレーヤ（デフォルトは人間）
  boolean bflag = false;     // 黒番プレーヤ（デフォルトは人間）

  Board[] barr = new Board[120];  // Board を保存する配列を用意
  int barr_pt = 0;                // 配列のインデックス用の変数

  KeyIn kin = new KeyIn(); // Key入力用オブジェクト
  Board bd  = new Board(); // Boardデータ用
  Point newpt;             // Point一次保存用

	
    frame.setTitle("Othello_Board");
    frame.setSize(470, 500);
    frame.addWindowListener(new WinCloser());

    JPanel basepanel = new JPanel();
    DBPanel cpanel = new DBPanel();
    JPanel lpanel = new JPanel();

    cpanel.setPreferredSize(new Dimension(450,430));
    cpanel.addMouseListener((MouseListener) cpanel);

    JButton passbutton = new JButton("Pass");
    passbutton.setActionCommand("00");
    passbutton.addActionListener((ActionListener) cpanel);
    JButton undobutton = new JButton("Undo");
    undobutton.setActionCommand("99");
    undobutton.addActionListener((ActionListener) cpanel);

    lpanel.add(wscore);
    lpanel.add(passbutton);
    lpanel.add(undobutton);
    lpanel.add(bscore);

    basepanel.add(cpanel, "Center");
    basepanel.add(lpanel, "South");

    Container contentPane = frame.getContentPane();
    contentPane.add(basepanel);

    frame.setVisible(true);

    label1:
    do {
      barr[barr_pt++] = bd; // Board を一手ごとに保存しておく

      bd.printboard(); //boardの印刷
      if(vflag) bd.printnextmove(); //次に打てる場所を印刷(デバッグ)

      if((bd.isturnblack() && !bflag)
         || (!bd.isturnblack() && ! wflag)) { 
        /* 黒番で bflag==false または 白番で wflag==false のときは，
           人間が打つ */   
        do {
          if(vflag) {  //コンピュータが計算した次の手を印刷(デバッグ)
            newpt = bd.findbesthand();
            System.out.println(" " + newpt);	
          }
          System.out.print("Next move: "); //プロンプトを表示して
          newpt = cpanel.point; //次の手の入力
          if (newpt.equals(Point.MATTA)) { //待ったの処理
            if(barr_pt >= 3) {     // 3手め以降ならば戻せる
              bd = barr[barr_pt - 3]; // 配列から前の状態を取りだす
              barr_pt -= 3;  // インデックスを戻す
              //continue label1;  // label1 から処理を再開
            }
          }
        } while(bd.ispointok(newpt) == false); //手が正しくない場合は繰り返す
      } else {
        newpt = bd.findbesthand();
        System.out.println("Next move: " + newpt + " selected");
      }
      bd = bd.domove(newpt); //手を打って，新しいBoardを作る
    } while(bd.isgameover() == false); //ゲームオーバーでない限りつづける
    bd.printboard(); //終了時の盤面を表示する  
    //board = bd.bbb();
    //repaint();
  }
}
