/**
 *  <code>Board</code> は，オセロの盤面を表現するクラスである．
 *  ver.2 evalw を加えたバージョン
 *
 * @author  Shogo Matsui
 * @version 0.032, 11/10/2014
 */
public class Board {
  private int[][] board;  
  private Nextmove nextmv;
  private int hand; /* hand 1, -1 */
  private int depth;
  //private int sum = 0;
  int[][] wbd  =  {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 30, -12, 0, -1, -1, 0, -12, 30, 0},
                   { 0, -12, -15, -3, -3, -3, -3, -15, -12, 0},
                   { 0, 0, -3, 0, -1, -1, 0, -3, 0, 0},
                   { 0, -1, -3, -1, -1, -1, -1, -3, -1, 0},
                   { 0, -1, -3, -1, -1, -1, -1, -3, -1, 0},
                   { 0, 0, -3, 0, -1, -1, 0, -3, 0, 0},
                   { 0, -12, -15, -3, -3, -3, -3, -15, -12, 0},
                   { 0, 30, -12, 0, -1, -1, 0, -12, 30, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} };
  int[][] sbd  =  { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    { 0, 20, 10, 5, 0, 0, 5, 10, 20, 0},
                    { 0, 10, 10, 0, 0, 0, 0, 10, 10, 0},
                    { 0,  5,  0, 5, 0, 0,  5, 0,  5, 0},
                    { 0,  0,  0, 0,-1, 1, 0, 0, 0, 0},
                    { 0,  0,  0, 0, 1,-1, 0, 0, 0, 0},
                    { 0,  5,  0, 5, 0, 0, 5, 0, 5, 0},
                    { 0, 10, 10, 0, 0, 0, 0, 10, 10, 0},
                    { 0, 20, 10, 5, 0, 0, 5, 10, 20, 0},
                    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} };

  private static final int[][] dxdy = {{ 1,1},{ 1,0},{ 1,-1},
                                       { 0,1},       { 0,-1},
                                       {-1,1},{-1,0},{-1,-1} }; 
  /**
   * ゲーム開始時のBoardを作成する．
   * ゲーム盤の配列(初期状態)，手数(4)，手番(黒)，
   * 次に打てる場所のデータNextmoveを初期化する．
   */
  public Board() {
    int[][] bd  = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 0, 0, 0,-1, 1, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 1,-1, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} };
                   
    board = bd;
    hand = 1;
    depth = 4;
    nextmv = new Nextmove(bd, hand);
  }
  /**
   * 配列データから新たなBoardを作成する
   * @param bd 新たなボードの配列データ
   * @param next 次のプレーヤ
   * @param dp 手数
   */
  public Board(int[][] bd, int next, int dp) {
    board = bd;
    hand = next;
    depth = dp;
    nextmv = new Nextmove(bd, hand);
  }
  /**
   * 黒の手番ならば真を返す．
   * @return 黒番ならばtrue
   */
  public boolean isturnblack() {
    return hand == 1;
  }
  public int[][] board(){
    return board;
  }
  public Point[] nextmv(){
    return nextmv.next();
  }
  /**
   * 手の正当性をチェックする(nextmvに含まれているかどうか調べる)．
   * パスの場合( (0,0) )も，打つ場所がないことを確認する．
   * @param next 石を打つ場所
   * @return 正しければtrue
   */
  public boolean ispointok(Point next) {
    if(nextmv.length() == 0 && next.equals(Point.PASS)){
      return true;
    }
    //System.out.println("here");
    return nextmv.search(next);
  }
  /**
   * ゲームが終了しているが調べる．
   * @return 終わりであればtrue
   */
  public boolean isgameover() {
    if(depth == 64)
      return true; // 64手打った．
    if(nextmv.length() != 0)
      return false; // 打つ手がある．
    else {
      Board nbd = domove(Point.PASS); // パスする.
      if(nbd.nextmv.length() == 0) // さらにパスか？
        return true;
    }
    return false;
  }
  /**
   * 石を打ち，次の盤面を作る．
   * @param nextPt 石を打つ場所
   * @return 新たな盤面(Boardオブジェクト)
   */
  public Board domove(Point nextPt) {
    int[][] nextbd = new int[10][10];
    /* 配列のコピー */
    for(int i = 0; i < 10; i++) {
      for(int j = 0; j < 10; j++) {
        nextbd[i][j] = board[i][j];
      }
    }
    /* 石を置いてひっくり返す */
    int nx = nextPt.x;
    int ny = nextPt.y;
    if ((nx == 0) && (ny == 0))  // 次の手が(0,0)の場合はパス
      return new Board(nextbd, -hand, depth) ; // handだけ変える
    /* 石を置く*/
    nextbd[nx][ny] = hand; // 石をnextPtの位置に打つ
    /* 石をひっくり返す */
    for(int dir = 0; dir < 8; dir++) { //8つの方向について以下を行う
      int dx = dxdy[dir][0];     //x,yの増分データを配列からとり出す
      int dy = dxdy[dir][1];
      int m = dx;                //増分データをm,nにセット
      int n = dy;
      while(hand + board[nx+m][ny+n] == 0) { //相手の石がある限り
        m += dx; n += dy;        //m,nを更新
      }
      if(hand == board[nx+m][ny+n]) {   //その先に自分の石があれば
        m -= dx; n -= dy;
        while(hand + board[nx+m][ny+n] == 0) { //逆戻りしながら
          nextbd[nx+m][ny+n] = hand;  //自分の石に変えていく
          m -= dx; n -= dy;
        }
      }
    }
    return new Board(nextbd, -hand, depth + 1);
  }
  /**
   * Boardを表示する．
   */
  public void printboard() {
    char ch = ' ';
    System.out.println();
    if(hand == -1)
      ch = 'O';
    else
      ch = 'X';
    System.out.println("+++ " + ch + "'s turn! ++ depth= " + depth);
    System.out.println("  1 2 3 4 5 6 7 8");
    int xscore = 0, oscore = 0;
    for(int j = 1; j <= 8; j++) {
      System.out.print("" + j);
	    
      for(int i = 1; i <= 8; i++) {
        if(board[i][j] == 0)
          ch = '.';
        if(board[i][j] == 1) {
          ch = 'X';
          xscore++;
        }
        if(board[i][j] == -1) {
          ch = 'O';
          oscore++;
        }
        System.out.print(" " + ch);
      }
      System.out.println();
    }
    System.out.println("score: O=" + oscore + ", X=" + xscore);
  }
  /**
   * 次に打てる場所(Nextmove)を表示する(デバッグ用)．
   */
  public void printnextmove() {
    System.out.println(nextmv);
  }
  /**
   * 次に打つべき手を探す．
   * @return 次に打つ場所
   */
  public Point findbesthand() {
    if(depth <= 6) return nextmv.getrand();
    else if(depth <= 20)  return monte(5000);
    else if(depth <= 52) return monte(10000);
    //else if(depth <= 25) return review(3);
    //else if(depth <= 30) return deep(3);
    //else if(depth <= 52) return review(4);
    //else if(depth <= 52) return review(6);
    //else if(depth <= 50) return weight(5);
    else if(depth <= 56) return evalw2(); // 最善手探索
    else return evalw(); //必手探索
    //  次に打つ場所を決めて，Pointを返す．
    //  たとえば，
    //  5-49手は，ランダムに手を決める，
    //  　50-64手は，evalw() を呼ぶ， など．
  }

  private Point monte(int loop){
    Point pnt;
    Point best = new Point(0, 0);
    int win = 0;
    int max = 0;
    if(nextmv.length() == 0)
      return Point.PASS ; /* パス */
    nextmv.resetPt(); // getsucの初期化
    if(nextmv.length() == 1)
      return nextmv.getsuc(); // 先頭を返す 
    while((pnt = nextmv.getsuc()) != null) { //nextmvの各Pointについて
       // System.out.print("" + pnt ); //処理中のPointの印刷(デバッグ用)
      Board nextbd = domove(pnt); //打って次の盤面を作る
      for(int i = 0; i < loop; i++){
        if(nextbd.randam() == false) win++;
      }
      //System.out.println("win:"+win);
      if(max < win){
        max = win;
        best = pnt;
      }
      
    }
    if(max == 0)  best = nextmv.getrand();
    //System.out.println("max:"+max);
    return best;
  }

  private boolean randam(){
    Point pnt;
    if(nextmv.length() > 0) { //パスでない場合
      nextmv.resetPt(); // getsucの初期化
      //while((pnt = nextmv.getsuc()) != null) { //nextmvの各Pointについて
      pnt = nextmv.getrand();
      Board nextbd = domove(pnt); //打って次の盤面を作る
      if (nextbd.randam() == false) return true; //勝つ場所があった
      //}
      return false;  //勝つ場所がなかった
    } else if (!isgameover()) { // パスの場合
      Board nextbd = domove(Point.PASS);  //パスとして新しい盤面を作り
      return !nextbd.randam();  // 再帰して，評価値を反転してから返す
    } else {  // ゲームオーバーのとき
      int score = 0; // スコアを数える
      for(int i = 1; i <= 8; i++) {
        for(int j = 1; j <= 8; j++) {
          score += board[i][j];
        }
        //System.out.println(score);
      }
      score = score * hand;
      // System.out.println(score);
      if(score > 0)
        return true; // スコアが大きいと勝ち
      else
        return false; // スコアが小さいか等しいと負け
    }
  }
  private Point review(int d){
    Point pnt;
    Point pnt2 = new Point(0, 0);
    Point best = new Point(0, 0);
    int k = 5;
    int A = 0;
    int B =0;
    int max=-999;
    int max2=-999;
    int score = 0;
    int me = 0, you = 0;
    int ms = -64, ys = -64;
    nextmv.resetPt();
    while((pnt = nextmv.getsuc()) != null){
      //System.out.println("hand:"+hand);
      //System.out.println("before:"+pnt);
      Board nextbd = domove(pnt); 
      me = nextbd.dlen(d, me);   you = nextbd.dlen(d+1, you); 
      //System.out.println("me:"+me);
      A = me - you;
      // System.out.println("me:"+me+"  you:"+you);
      ms = nextbd.dsum(hand, d, -ms);  ys = nextbd.dsum(hand, d+1, -ys);  
      //System.out.println("ms:"+ms);
      B = ms - ys;
      score = A + k*B;
      //System.out.println("score:"+score);
      if(max < score){
        max = score;
        best =pnt;
      }
      
    }
    // if(max == -999) best=pnt2; 
    //System.out.println("final:"+max);
    return best;
  }
  
  // private Point angel(){
  //   int score = 0;
  //   for(int i=1;i<=3;i++){
  //     for(int j=1;j<=3;j++){
        
  //     }
  //   }
  // }

  private int dlen(int d, int ab){
    int max = -65;
    int len = 65;
    Point pnt;
    int score = 0;
    //System.out.println("hand dlen:"+hand);
    if(nextmv.length() > 0 && d>0) { //パスでない場合
    nextmv.resetPt(); // getsucの初期化
    while((pnt = nextmv.getsuc()) != null) { //nextmvの各Pointについて
      //System.out.println('b');
      Board nextbd = domove(pnt); //打って次の盤面を作る
      //if(len<ab)   return max;
      len = nextbd.dlen(d-1, ab);

      if(len > max){
        max = len; 
        //System.out.println("max:"+max);
      }
    }
    return max;
    }
    else {  // ゲームオーバーのとき
      score = nextmv.length();
      //System.out.println("score:"+score);
      }
      // if(hand==1){
      //   System.out.println("score 1:"+score);
      // }
      // else{
      //   System.out.println("score -1:"+score);
      // }
      return score;
  }
  
  private int score(int h){
    int score = 0;
    if(board[1][1]==h)  score += 50;
    if(board[1][1]==-h)  score -= 30;
    if(board[1][8]==h)  score += 50;
    if(board[1][8]==-h)  score -= 30;
    if(board[8][1]==h)  score += 50;
    if(board[8][1]==-h)  score -= 30;
    if(board[8][8]==h)  score += 50;
    if(board[8][8]==-h)  score -= 30;
    if(board[3][1]==h)  score += 5;
    if(board[3][1]==-1*h && board[2][1]==h) score -= 10;
    if(board[3][8]==h)  score += 5;
    if(board[3][8]==-1*h && board[2][8]==h) score -= 10;
    if(board[6][1]==h)  score += 5;
    if(board[6][1]==-1*h && board[7][1]==h) score -= 10;
    if(board[6][8]==h)  score += 5;
    if(board[6][8]==-1*h && board[7][8]==h) score -= 10;
    if(board[1][3]==h)  score += 5;
    if(board[1][3]==-1*h && board[1][2]==h) score -= 10;
    if(board[8][3]==h)  score += 5;
    if(board[8][3]==-1*h && board[8][2]==h) score -= 10;
    if(board[1][6]==h)  score += 5;
    if(board[1][6]==-1*h && board[1][7]==h) score -= 10;
    if(board[8][6]==h)  score += 5;
    if(board[8][6]==-1*h && board[8][7]==h) score -= 10;
    if(board[3][3]==h)  score += 5;
    if(board[3][3]==-1*h && board[2][2]==h) score -= 10;
    if(board[6][6]==h)  score += 5;
    if(board[6][6]==-1*h && board[7][7]==h) score -= 10;
    if(board[6][3]==h)  score += 5;
    if(board[6][3]==-1*h && board[7][2]==h) score -= 10;
    if(board[3][6]==h)  score += 5;
    if(board[3][6]==-1*h && board[2][7]==h) score -= 10;
    if(board[1][1]==0){
      if(board[2][1]==h)  score -= 30;
      if(board[2][2]==h)  score -= 30;
      if(board[1][2]==h)  score -= 30;
    }
    if(board[1][8]==0){
      if(board[1][7]==h)  score -= 30;
      if(board[2][7]==h)  score -= 30;
      if(board[2][8]==h)  score -= 30;
    }
    if(board[8][1]==0){
      if(board[7][1]==h)  score -= 30;
      if(board[7][2]==h)  score -= 30;
      if(board[8][2]==h)  score -= 30;
    }
    if(board[8][8]==0){
      if(board[8][7]==h)  score -= 30;
      if(board[7][7]==h)  score -= 30;
      if(board[7][8]==h)  score -= 30;
    }
    return score;
  }
  private int dsum(int h, int d, int ab){
    int score=0;
    int min = 100;
    int value=-65;
    Point pnt;
    if(nextmv.length() > 0 && d>0) { //パスでない場合
      nextmv.resetPt(); // getsucの初期化
      while((pnt = nextmv.getsuc()) != null) { //nextmvの各Pointについて
        //System.out.println('b');
        Board nextbd = domove(pnt); //打って次の盤面を作る
        //if(value>ab)   return -1*min;
        value = nextbd.dsum(h,d-1,value);
        if(value < min){
          min = value; 
          //System.out.println(d+"'s score"+min);
        }
      }
      return -min;
      }
    else {  // ゲームオーバーのとき
      // for(int i=1; i<=8; i++){
      //   for(int j=1; j<=8; j++){
      //     score += board[i][j];
      //     //if(board[i][j] == h) score += board[i][j];
      //   }
      // }
      score += score(h);
    }
    //System.out.println("score:"+score);
    //score = score*hand;
    return score*-1;
  }
  private Point weight(){
    Point pnt;
    // if(nextmv.length == 0) return Point.PASS;
    nextmv.resetPt();
    Point maxP = new Point(0, 0);
    int max = -100;
    int value = 0;
    while((pnt = nextmv.getsuc()) != null){
      value = wbd[pnt.x][pnt.y];
      //System.out.println("value:"+value);
      if(value > max ){
        max = value;
        maxP = pnt;
        //System.out.println("max:"+max);
        //sum = sum(wbd, 2);
      }
    }
    
    // sum += max;
    // System.out.println(sum);
    return maxP;
  }
  
  
 
  /**
   * evalw は，必勝手を選択する．
   * 必勝手が見つからない場合はランダムに手を選択する．
   * @return  次の手のPoint
   */
  private Point evalw() {
    Point pnt;
    if(nextmv.length() == 0)
      return Point.PASS ; /* パス */
    nextmv.resetPt(); // getsucの初期化
    if(nextmv.length() == 1)
      return nextmv.getsuc(); // 先頭を返す 
    while((pnt = nextmv.getsuc()) != null) { //nextmvの各Pointについて
       // System.out.print("" + pnt ); //処理中のPointの印刷(デバッグ用)
      Board nextbd = domove(pnt); //打って次の盤面を作る
      if(nextbd.bevalw() == false) { //勝つ場所が見つかったら
        // System.out.print("win ");     //"win" と印刷(デバッグ用)
        return pnt;  //位置情報(Point)を返す
      }
    }
    /* 負けの場合 */
    // System.out.print("lose "); //"lose" と印刷(デバッグ用)
    //return nextmv.getrand(); //ランダムに選ぶ
    return nextmv.getrand();
  }
  /**
   * bevalw は，盤面の勝ち負けを判定する．
   * @return  勝ちのとき true  負けのとき false
   */
  private boolean bevalw() {
    Point pnt;
    if(nextmv.length() > 0) { //パスでない場合
      nextmv.resetPt(); // getsucの初期化
      while((pnt = nextmv.getsuc()) != null) { //nextmvの各Pointについて
        Board nextbd = domove(pnt); //打って次の盤面を作る
        if (nextbd.bevalw() == false)
          return true; //勝つ場所があった
      }
      return false;  //勝つ場所がなかった
    } else if (!isgameover()) { // パスの場合
      Board nextbd = domove(Point.PASS);  //パスとして新しい盤面を作り
      return !nextbd.bevalw();  // 再帰して，評価値を反転してから返す
    } else {  // ゲームオーバーのとき
      int score = 0; // スコアを数える
      for(int i = 1; i <= 8; i++) {
        for(int j = 1; j <= 8; j++) {
          score += board[i][j];
        }
        //System.out.println(score);
      }
      score = score * hand;
      // System.out.println(score);
      if(score > 0)
        return true; // スコアが大きいと勝ち
      else
        return false; // スコアが小さいか等しいと負け
    }
  }

  private Point evalw2() {
    Point pnt;
    Point x = new Point(0, 0);
    int man=-64;
    int value=-64;
    
    if(nextmv.length() == 0){
      
      return Point.PASS ; /* パス */
    }
      nextmv.resetPt(); // getsucの初期化
    if(nextmv.length() == 1)
      return nextmv.getsuc(); // 先頭を返す 
    while((pnt = nextmv.getsuc()) != null) { //nextmvの各Pointについて
      // System.out.print("" + pnt ); //処理中のPointの印刷(デバッグ用)
      
      Board nextbd = domove(pnt); //打って次の盤面を作る
      value = nextbd.ievalw(-1*value);
      //value = nextbd.ievalw();
      //System.out.println('a');
      if(man < value){
        man = value;
        x = pnt;
      }
    }
    //System.out.println("minmin;"+min);
    return x; //ランダムに選ぶ
  }

  private int ievalw(int ab) {
    int min = 64;
    int val = -64;
    Point pnt;
    //System.out.println('b');
    if(nextmv.length() > 0) { //パスでない場合
      nextmv.resetPt(); // getsucの初期化
      while((pnt = nextmv.getsuc()) != null) { //nextmvの各Pointについて
        // if(wbd[pnt.x][pnt.y] == 30 && nextmv.length() != 0){
        //   return hand*65;
        // }
        Board nextbd = domove(pnt); //打って次の盤面を作る
        if(val>ab)   return -1*min;
        val = nextbd.ievalw(-1*val);
        //val = nextbd.ievalw();
        if(val < min){
          min = val; 
          //System.out.println("min:"+min);
        }
      }
      return -1*min;
    }else if (!isgameover()) { // パスの場合
      //System.out.println('a');
      Board nextbd = domove(Point.PASS);  //パスとして新しい盤面を作り
      return nextbd.ievalw(val*-1);  // 再帰して，評価値を反転してから返す
      //return nextbd.ievalw();
    }else {  // ゲームオーバーのとき
      int score = 0; // スコアを数える
      for(int i = 1; i <= 8; i++) {
        for(int j = 1; j <= 8; j++) {
          score += board[i][j];
        }
      // if(board[1][1] == hand) score += 30;
      // if(board[1][8] == hand) score += 30;
      // if(board[8][8] == hand) score += 30;
      // if(board[8][1] == hand) score += 30;

        //System.out.println(score);
      }

      score = score * hand;
      
      //System.out.println("score:" + score); 
      return score*-1;
    }
      // System.out.println(score);    
  }
}