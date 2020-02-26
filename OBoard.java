/**
 * @version 0.02 7 Dec. 2015
 * @author Shogo Matsui
 */
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

class BoardPanel extends JPanel implements ActionListener {
  /* volatile */ boolean dataready = false;
  int inputI;
  int inputJ;
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
  int[][] first = board;
  ImageIcon empty = new ImageIcon("./empty.gif");
  ImageIcon black = new ImageIcon("./black.gif");
  ImageIcon white = new ImageIcon("./white.gif");
  ImageIcon nextmv = new ImageIcon("./next.gif");
  JButton[][] jb = new JButton[10][10];

  BoardPanel() {
    setLayout(new GridLayout(8 ,8));
    setPreferredSize(new Dimension(496,496));

    for(int j = 1; j <= 8; j++)
      for(int i = 1; i <= 8; i++) {
        jb[i][j] = new JButton(empty);
        jb[i][j].setActionCommand("" + j + i);
        jb[i][j].addActionListener(this);
        jb[i][j].setBorderPainted(false);
        add(jb[i][j]);
      }
    renewboard();
  }
  private void renewboard() {
    int b = 0;
    int w = 0;
    for(int i = 1; i <= 8; i++)
      for(int j = 1; j <= 8; j++) {
        switch(board[i][j]) {
        case 0:
          jb[i][j].setIcon(empty);
          break;
        case 1:
          jb[i][j].setIcon(black);
          b++;
          break;
        case -1:
          jb[i][j].setIcon(white);
          w++;
          break;
        }
      }
    OBoard.wscore.setText("White:" + ( w/10 ) + ( w%10 ));
    OBoard.bscore.setText("Black:" + ( b/10 ) + ( b%10 ));
  }	

  //次におくことができる位置の表示
  public void next(Point[] next){
    int x;
    int y;
    for(int i = 0; i < next.length; i++){
      if(next[i] != null){
        x = next[i].x;
        y = next[i].y;
        jb[x][y].setIcon(nextmv);
      }
    }
  }

  /**
   * 与えられた配列データを画面に表示する
   * @param newbd 印刷すべきボードの配列データ
   */
  public void printboard(int[][] newbd) {
    board = newbd;
    //System.out.println("bd:"+board[5][6]);
    renewboard();
  }
  
  public void actionPerformed(ActionEvent evt) {
    String position = evt.getActionCommand();
    int i = Character.digit(position.charAt(0), 10);
    int j = Character.digit(position.charAt(1), 10);
    System.out.println("i=" + i + " j=" + j);
    //System.out.println((String)evt.getSelectedItem());
    synchronized(this) {   // 危険部分　はじまり
      inputI = i;
      inputJ = j;
      dataready = true;
      notifyAll();
    }                     // 危険部分　おわり
    //printboard(OBoard.b);
    //board = OBoard.b;
    //System.out.println(position);
  }
  

  /**
   * 次のPointをlistenerから読み込む
   * @return 読み込んだPoint
   */
  public Point readpoint() {
    int i, j;

    synchronized(this) {     // 危険部分　はじまり
      while(dataready == false) {
        try {
          wait();     // notify待ち
        } catch(InterruptedException e){}
      }
      i = inputI;
      j = inputJ;
      dataready = false;
    }                        // 危険部分　おわり
    return new Point( j, i ); //Pointを作って返す
  }

  final int dx = 50; // ボードのマスの間隔x
  final int dy = 50; // ボードのマスの間隔y

}

class Game extends JPanel implements ActionListener{
  String state = "";
  int num = 1;
  public void actionPerformed(ActionEvent evt){
    String str = evt.getActionCommand();
    state = str;
    System.out.println(state);
    if(state.equals("exit"))  System.exit(0);
  }
}

public class OBoard {
  static JLabel wscore = new JLabel();
  static JLabel bscore = new JLabel();

  static public boolean vflag = false;
  static int[][] b;
  DefaultComboBoxModel model;
  //Board bd  = new Board(); // Boardデータ用

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setTitle("Othello Board");
    frame.setSize(550,800);
    frame.addWindowListener(new Terminator());

    // JPanel object
    JPanel basepanel = new JPanel();
    BoardPanel cpanel = new BoardPanel();
    JPanel lpanel = new JPanel();
    Game game = new Game(); 

    JButton passbutton = new JButton("Pass");
    passbutton.setActionCommand("00");
    passbutton.addActionListener((ActionListener) cpanel);
    JButton undobutton = new JButton("Undo");
    undobutton.setActionCommand("99");
    undobutton.addActionListener((ActionListener) cpanel);
    //  スタート、ストップ、終了の処理
    // JComboBox gamecombo = new JComboBox();
    // gamecombo.addActionListener((ActionListener) game);
    // gamecombo.addItem("");
    // gamecombo.addItem("Start");
    // gamecombo.addItem("Stop");
    // gamecombo.addItem("Exit");
    // gamecombo.setActionCommand("Exit");
    //黒プレイヤーの選択  
    JComboBox blackcombo = new JComboBox();
    blackcombo.addItem("Human");
    blackcombo.addItem("Computer");
    //白プレイヤーの選択
    JComboBox whitecombo = new JComboBox();
    whitecombo.addItem("Human");
    whitecombo.addItem("Computer");

    JPanel combo = new JPanel();
    // combo.add(new JLabel("Game:"));
    // combo.add(gamecombo);
    combo.add(new JLabel("  Black:"));
    combo.add(blackcombo);
    combo.add(new JLabel("  White:"));
    combo.add(whitecombo);
    combo.add(new JLabel("     Game:"));
    //combo.add(new JLabel("          "));
    JPanel gamepanel = new JPanel();
    JButton startbutton = new JButton("Start");
    startbutton.setActionCommand("start");
    startbutton.addActionListener((ActionListener) game);
    JButton exitbutton = new JButton("Exit");
    exitbutton.setActionCommand("exit");
    exitbutton.addActionListener((ActionListener) game);
    gamepanel.add(startbutton);
    gamepanel.add(exitbutton);
    

    lpanel.add(combo);
    lpanel.add(gamepanel);
    lpanel.add(wscore);
    lpanel.add(passbutton);
    lpanel.add(undobutton);
    lpanel.add(bscore);
    //lpanel.add(new JLabel(""));
    //lpanel.add(gamepanel);
    //lpanel.add(new JLabel("\n"));

    basepanel.add(combo, "North");
    basepanel.add(gamepanel, "North");
    basepanel.add(cpanel, "Center");
    basepanel.add(lpanel, "Center");
    //basepanel.add(gamepanel, "South");
    
    Container cp = frame.getContentPane();
    cp.add(basepanel);

    frame.setVisible(true);
    //

    //  wb,start処理
    String white = "";
    String black = "";
    String arguments[] = new String[2];
    //int start = 0;
    do{
      if(whitecombo.getSelectedIndex() == 1)  white = "w";
      if(whitecombo.getSelectedIndex() == 0)  white = "";
      if(blackcombo.getSelectedIndex() == 1)  black = "b";
      if(blackcombo.getSelectedIndex() == 0)  black = "";
      arguments[0] = "-" + white + black;
    //}while(gamecombo.getSelectedIndex() != 1);
    }while(!game.state.equals("start"));
    
    arguments[1] = "-verbose";
    //System.out.println("len"+arguments.length);
    
    //変数定義
    boolean wflag = false;     // 白番プレーヤ（デフォルトは人間）
    boolean bflag = false;     // 黒番プレーヤ（デフォルトは人間）

    Board[] barr = new Board[120];  // Board を保存する配列を用意
    int barr_pt = 0;                // 配列のインデックス用の変数

    KeyIn kin = new KeyIn(); // Key入力用オブジェクト
    Board bd  = new Board(); // Boardデータ用
    Point newpt;             // Point一次保存用

    //System.out.println(bd.wbd[1][1]);
    /* オプション引数の処理 */
    int i = 0;
    while (i < arguments.length && arguments[i].startsWith("-") ) { // "-"で始まる場合
      String arg = arguments[i++];  //　文字列を取り出す
      //System.out.println("arg:" + arg);
      /* verbose flag check */
      if(arg.equals("-verbose")) {   // -verbose ならば
        System.out.println("verbose mode on");  //表示してから
        vflag = true;               // vflagをtrueに
      }

      /* 1 character flag chack */
      else {
        for(int j = 1; j < arg.length(); j++) { //1文字ずつ
          char flag = arg.charAt(j);  // flagに取り出す
          switch(flag) {
          case 'w':   // wフラグの処理
            wflag = true;  // wflagをtrueに
            break;
          case 'b':   // bフラグの処理
            bflag = true;  // bflagをtrueに
            break;
          default:    // それら以外はエラーメッセージを出す
            System.err.println("Oth: illegal option " + flag);
            break;
          }
        }
      }
      //System.out.println(wflag);
    }
    if(i != arguments.length)  // "-"で始まらない引数が残っている場合
      System.err.println("Usage: Oth [-verbose] [-wb]");
    else // vflagがtrue ならば情報を表示する 
      if(vflag)
        System.out.println(
          "options: verbose=" + vflag + " b=" + bflag + " w=" + wflag);

    /* main loop */
    //b = bd.board();
    label1:
    do {
      //if(gamecombo.getSelectedIndex() == 3) System.exit(0);
      barr[barr_pt++] = bd; // Board を一手ごとに保存しておく
      cpanel.printboard(bd.board());
      bd.printboard(); //boardの印刷
      
      //System.out.println("arg:"+arguments[1]);
      //System.out.println(whitecombo.getSelectedIndex());
      if(vflag) {
        //bd.printnextmove(); //次に打てる場所を印刷(デバッグ)
        Point[] nextmv = bd.nextmv();
        cpanel.next(nextmv);
        //System.out.println("len:"+nextmv[5]);
      }
      if((bd.isturnblack() && !bflag)
         || (!bd.isturnblack() && ! wflag)) { 
        /* 黒番で bflag==false または 白番で wflag==false のときは，
           人間が打つ */   
        do {
          if(vflag) {  //コンピュータが計算した次の手を印刷(デバッグ)
            newpt = bd.findbesthand();
            //System.out.println(" " + newpt);	
          }
          System.out.print("Next move: "); //プロンプトを表示して
          //newpt = kin.readpoint(); //次の手の入力
          newpt = cpanel.readpoint();
          //if(gamecombo.getSelectedIndex() == 0) break;
          //cpanel.board = bd.board();
          // cpanel.printboard(bd.board());
          if (newpt.equals(Point.MATTA)) { //待ったの処理
            if(barr_pt >= 3) {     // 3手め以降ならば戻せる
              bd = barr[barr_pt - 3]; // 配列から前の状態を取りだす
              barr_pt -= 3;  // インデックスを戻す
              continue label1;  // label1 から処理を再開
            }
          }
        } while(bd.ispointok(newpt) == false); //手が正しくない場合は繰り返す
        //b=bd.board();
        //if(gamecombo.getSelectedIndex() == 0) break;
      } else {
        newpt = bd.findbesthand();
        System.out.println("Next move: " + newpt + " selected");
      }
      bd = bd.domove(newpt); //手を打って，新しいBoardを作る
    } while(bd.isgameover() == false); //ゲームオーバーでない限りつづける
    cpanel.printboard(bd.board());
    bd.printboard(); //終了時の盤面を表示する
  

  }
}

class Terminator extends WindowAdapter {
  public void windowClosing(WindowEvent e) {
    System.exit(0);
  }
}
