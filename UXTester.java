
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Robot;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/*
 * 
 * 교수님께서 보여주신 예제에서는 테스트 케이스의 라벨 숫자와 함께 위치도 랜덤하게 바뀌기 때문에
 * 개인마다 바뀐 숫자의 위치를 찾아내는 반응속도나 순발력 등의 요소가 테스트 값에 영향을 줄 것이라고 생각했습니다. 
 * 따라서 고정된 여덟 개의 테스트용 텍스트들과, 각자 고정된 위치에서
 * 주어진 랜덤한 숫자를 찾아서 버튼을 누르는 것으로 시나리오를 변경했습니다.
 * 또한 마우스를 이용한 컴퓨터 실행환경에서, 반응속도와 우연에 의한 요소를 최대한 배제하기 위해서
 * 한 번의 테스트 케이스를 입력할 때마다 마우스의 위치를 초기화 시켰습니다.
 * 
 * 또한 숫자보다는 라벨에서 많이 볼 수 있는 한국어와 영어도 함께 테스트하면 
 * 더 신뢰도 있는 결과를 얻을 수 있다고 생각해 텍스트도 추가하여 출력하였습니다.
 * 0~9를 모두 포함하는 총 여덟 개의 한 자리, 두 자리, 세 자리의 숫자가 테스트 케이스로 존재합니다.
 * 숫자와 함께 해당 숫자를 나타내는 텍스트가 한글, 영어 중 랜덤하게 나타납니다.
 * 사용자가 텍스트의 길이로 숫자를 반응적으로 유추할 수 있기에
 * 임의의 활자를 추가해 모든 텍스트의 길이를 비슷하게 정규화하였습니다.
 * 
 * 모든 숫자와 텍스트 라벨이 랜덤한 사이즈 값으로 총 40번 등장합니다.
 * 마지막에 테스트 결과가 출력됩니다.
 * 테스트 결과가 출력되면 다른 기능은 동작하지 않습니다.
 * 프로그램을 종료한 후 재실행 시켜주세요.
 * 
 */

public class UXTester {

	static long startTime = 0;
	static long endTime = 0;

	// *
	// ***
	// 테스트 출력용 텍스트
	static String testerNums[] = { "2", "3", "4", "7", "13", "59", "67", "180" };
	static String testerTexts[][] = { { "둘입니다", "셋입니다", "넷입니다", "이일고옵", "열하고셋", "오십아홉", "예순일곱", "백여드은" },
			{ "number-two", "number-three", "number-four", "number-seven", "thirte-een", "fiff-nine", "six-seven",
					"hund-egt" } };

	// 사용할 문자열 초기 세팅
	static String openHTMLTag = "<html><body style = 'text-align:center;'>";
	static String closingHTMLTag = "</body></html>";
	static String nextIndicater = "<br><br><small,i>클릭하여 다음으로 진행하기</small,i>";
	static String guideTexts[] = { " 이 프로그램은 스마트폰 UI에서 어떤 라벨 텍스트 사이즈가<br>사용자에게 가장 빨리 인식되는지를 테스트해보는 프로그램 입니다.",
			"아래 버튼에 보이는 총 여덟 개의 숫자가 글자와 함께<br> 10pt~20pt 사이의 랜덤한 사이즈로 나타납니다.",
			"총 40개의 응답이 한 세트이며<br>5초마다 하나의 케이스가 등장합니다.",
			"3초마다 화면 중앙에 새로 아이콘이 나타나면 <br>최대한 빠르게 화면 아래에서<br>동일한 숫자의 버튼을 찾아 눌러주세요.",
			"버튼을 눌러도 바로 다음으로 넘어가지 않으며<br>3초가 지나면 진행됩니다.<br> 오류 방지를 위해 모든 버튼은 한 번씩만 클릭해 주세요!",
			"3초 내로 버튼을 입력하지 않을 시<br>자동으로 다음 순서로 넘어갑니다.<br><br>준비 되셨나요?" };
	static int index = 0;
	static boolean delayToNext = false;

	/*
	 * 테스트 케이스용 변수
	 */
	static double testAnswerOutput[][] = new double[41][2]; // 실제 테스트값 (폰트사이즈)저장용 배열 (정답버튼,폰트사이즈 순서)
	static double testUserInput[][] = new double[41][3]; // 사용자가 입력한 테스트값 저장용 배열 [n 번째 케이스][{누른버튼,폰트사이즈, 타임}];
	static ArrayList<Double> testResult = new ArrayList<>();//

	static int count = 0;// 현재 테스트 케이스 count 변수
	static int testCaseIndex = 0; // 현재 테스트 케이스 지정할 변수
	static double testTimeValue = 0;// 응답한 시간 저장할 변수
	static int randomIndex; // 랜덤하게 출력될 숫자 인덱스 변수 (0~7)
	static int randomSize; // 랜덤 폰트 사이즈 변수 (10~19)

	static String curTestLabel = "ready"; // 출력할 테스트용 라벨텍스트
	static String curGuideText = openHTMLTag + guideTexts[index] + nextIndicater + closingHTMLTag;// 출력할 가이드 텍스트

	//
	// ***
	// *

	JFrame smartphoneGUI = new JFrame(); // 최상위 윈도우 프레임

	// 부모 컨테이너
	JPanel northPanel = new JPanel();
	JPanel centerPanel = new JPanel();
	JPanel southPanel = new JPanel();

	// northPanel 컴포넌트
	JPanel statusBar = new JPanel();
	JPanel screenSelector = new JPanel();

	// centerPanel 컴포넌트
	JPanel testCasePresenter = new JPanel();// 컨테이너
	JLabel textLabel = new JLabel("19011891 김나은                                  		           statusBar");
	JPanel appIcon = new JPanel();
	JPanel labelTextPanel = new JPanel(); // 컨테이너
	JLabel labelText = new JLabel(curTestLabel);
	JPanel obsolete = new JPanel();

	// southPanel 컴포넌트
	JPanel guideContentPanel = new JPanel();// 컨테이너
	JLabel guideTextLabel = new JLabel(curGuideText);
	JPanel submitButtons = new JPanel();

	UXTester() {

		smartphoneGUI.setResizable(false);
		smartphoneGUI.setTitle("LabelFontSizeTester");
		smartphoneGUI.setSize(375, 812);// 스마트폰 화면 크기 설정
		smartphoneGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 최상위 BorderLayout 설정
		smartphoneGUI.setLayout(new BorderLayout());
		smartphoneGUI.add(northPanel, BorderLayout.NORTH);

		smartphoneGUI.add(southPanel);
		smartphoneGUI.add(southPanel, BorderLayout.SOUTH);

		northPanel.setBackground(new Color(255, 255, 255));
		centerPanel.setBackground(new Color(255, 255, 255));
		southPanel.setBackground(new Color(200, 200, 255));

		/*
		 * North Panel
		 */
		// 상단바 statusBar 판넬 추가
		northPanel.setLayout(new FlowLayout());
		statusBar.setBackground(new Color(0, 0, 0));
		statusBar.setPreferredSize(new Dimension(smartphoneGUI.getWidth(), 30));

		// 내부에 텍스트 추가할 라벨 추가
		textLabel.setForeground(new Color(255, 255, 255));
		statusBar.add(textLabel);

		// 운영체제 화면사이즈 설정할 판넬 추가
		screenSelector.setPreferredSize(new Dimension(smartphoneGUI.getWidth(), 100));
		screenSelector.setBackground(northPanel.getBackground());

		JRadioButton androidButton = new JRadioButton("Android"); // 하위 환경 선택 컴포넌트
		JRadioButton iosButton = new JRadioButton("ios");// 하위 환경 선택 컴포넌트
		androidButton.setPreferredSize(new Dimension(100, 50));
		androidButton.setSelected(false);
		iosButton.setPreferredSize(new Dimension(100, 50));
		iosButton.setSelected(true);

		screenSelector.add(iosButton);
		screenSelector.add(androidButton);

		northPanel.add(statusBar);
		northPanel.add(screenSelector);

		/*
		 * center Panel
		 */
		centerPanel.setLayout(new FlowLayout());

		// 가운데 테스트 폰트 보여줄 컨테이너 추가
		testCasePresenter.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		testCasePresenter.setPreferredSize(new Dimension(300, 100));
		testCasePresenter.setBackground(centerPanel.getBackground());// 핵빨간색으로 했는데 왜 안보이지?

		// 패널 안에 앱 아이콘 역할 컴포넌트 추가
		appIcon.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		appIcon.setPreferredSize(new Dimension(48, 48));
		appIcon.setBackground(randomColor()); // 일단 검정색 - 나중에 랜덤컬러로 수정할 것
		testCasePresenter.add(appIcon);

		// 패널 안에 라벨 폰트 출력할 컴포넌트 추가
		labelTextPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		labelTextPanel.add(labelText);
		labelText.setPreferredSize(new Dimension(300, 40));
		labelText.setHorizontalAlignment(SwingConstants.CENTER);
		testCasePresenter.add(labelText);

		obsolete.setBackground(centerPanel.getBackground());
		obsolete.setPreferredSize(new Dimension(smartphoneGUI.getWidth(), 100));

		testCasePresenter.setVisible(false);
		centerPanel.add(obsolete);
		centerPanel.add(testCasePresenter);
		// testCasePresenter.setAlignmentY(SwingConstants.BOTTOM);
		smartphoneGUI.add(centerPanel, BorderLayout.CENTER);

		/*
		 * 
		 * South Panel
		 * 
		 */
		southPanel.setLayout(new GridLayout(2, 1));

		// 안내문구 띄워줄 가이드창
		guideContentPanel.setSize(smartphoneGUI.getWidth(), 80);
		guideTextLabel.setPreferredSize(new Dimension(smartphoneGUI.getWidth(), 150));
		guideTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
		guideContentPanel.add(guideTextLabel);

		// 테스트 케이스 입력할 버튼
		submitButtons.setPreferredSize(new Dimension(smartphoneGUI.getWidth(), 150));
		submitButtons.setLayout(new GridLayout(2, 4));
		JButton buttons[] = new JButton[8];
		int[] i = { 0 };
		for (i[0] = 0; i[0] < 8; i[0]++) {
			buttons[i[0]] = new JButton("<html><font style ='font-size: 20pt'>" + testerNums[i[0]] + "</font></html>");
			buttons[i[0]].setBackground(new Color(80, 200, 80));
			// 각 버튼 누르면 테스트값 저장되게 이벤트리스너 추가
			buttons[i[0]].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// 버튼을 누른 시간 측정
					endTime = System.currentTimeMillis();
					testUserInput[count][2] = endTime - startTime;
					testUserInput[count][0] = i[0];
					testUserInput[count][1] = randomSize;
					System.out.println(testUserInput[count][2]);

				}
			});
			submitButtons.add(buttons[i[0]]);
		}

		southPanel.add(guideContentPanel);
		southPanel.add(submitButtons);

		smartphoneGUI.setVisible(true);

		/*
		 * 
		 * 이벤트 핸들링
		 * 
		 */
		// 안드로이드, 맥 각각 버튼 클릭 시 스크린 사이즈가 변경되도록 이벤트리스너 추가
		androidButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Android 버튼 클릭 시 화면 크기 변경
				smartphoneGUI.setSize(360, 800);
				iosButton.setSelected(false);
				smartphoneGUI.revalidate();
			}
		});

		iosButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// iOS 버튼 클릭 시 화면 크기 변경
				smartphoneGUI.setSize(375, 812);
				androidButton.setSelected(false);
				smartphoneGUI.revalidate();
			}
		});

		// 가이드텍스트 영역 진행용 마우스리스너
		guideContentPanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// Start a Swing Timer when the mouse is released
				Timer timer = new Timer(50000000, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						index++;
						System.out.println(index);

						if (index == 6) {
							curGuideText = openHTMLTag + nextIndicater + closingHTMLTag;
							smartphoneGUI.revalidate();
						}

						if (index > guideTexts.length) {
							try {
								curGuideText = "클릭하여 테스트 시작하기";
								testCasePresenter.setVisible(true);
								guideTextLabel.setText(curGuideText); // 가이드 라벨 업데이트
								southPanel.update(southPanel.getGraphics());
								testStart();
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}
						}

						if (index < 6)
							curGuideText = openHTMLTag + guideTexts[index] + nextIndicater + closingHTMLTag;
						guideTextLabel.setText(curGuideText);
					}
				});

				// Start the timer
				timer.setInitialDelay(0);
				timer.start();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});

	}

	public void testStart() throws InterruptedException {

		System.out.println("test start");

		int delay = 2800; // 딜레이 텀 3초라 쓰고 2.8초
		int totalCase = 40; // 총 진행할 테스트 케이스 개수
		guideContentPanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

				Timer timer = new Timer(delay, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (count < totalCase) {
							try {
								setTest();
							} catch (AWTException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} //////////////////////////////////////// trigger
							centerPanel.update(centerPanel.getGraphics());
							curGuideText = (count + 1) + " / 40";
							guideTextLabel.setText(curGuideText);

							// Add code for your tests here
							System.out.println(count + "th");
							count++;

						} else {
							/////////////////////////////// 테스트케이스 끝난 이후 코드
							((Timer) e.getSource()).stop();
							for (int i = 0; i < totalCase + 1; i++) {
								System.out.println((i + 1) + "th input ) case : " + testUserInput[i][0] + "  /  size : "
										+ testUserInput[i][1] + "  /  time : " + testUserInput[i][2]);
							}

							// 응답시간 짧은 순서대로 오름차순 정렬
							for (int i = 1; i < totalCase; i++) {
								for (int j = i + 1; j < totalCase + 1; j++) {
									if (testUserInput[i][2] > testUserInput[j][2]) {
										double tmp[][] = new double[1][3];
										tmp[0] = testUserInput[i];
										testUserInput[i] = testUserInput[j];
										testUserInput[j] = tmp[0];
									}
								}
							}

							// 출력용
							JLabel resultTextPrintLabel = new JLabel();

							String result = openHTMLTag + "<small>";
							for (int i = 0; i < totalCase + 1; i++) {
								if (testUserInput[i][2] != 0)
									result += "[" + (i) + "th] " + (testUserInput[i][2] / 1000) + "sec at "
											+ testUserInput[i][1] + "pt";
								if (i % 2 == 0)
									result += "<br>";
								else
									result += "  /  ";
							}

							for (int i = 0; i < 5; i++) {
								if (testUserInput[i][2] != 0) {
									labelText
											.setText((int) testUserInput[i][1] + "pt size recognizes you most quickly");
									break;
								}
							}
							result += "</small>" + closingHTMLTag;
							guideTextLabel.setText("테스트 종료");
							guideContentPanel.update(guideContentPanel.getGraphics());
							centerPanel.update(centerPanel.getGraphics());

							resultTextPrintLabel.setText(result);
							appIcon.setVisible(false);
							//
							obsolete.setVisible(false);
							labelTextPanel.setVisible(false);
							testCasePresenter.add(resultTextPrintLabel);
							testCasePresenter
									.setPreferredSize(new Dimension(centerPanel.getWidth(), centerPanel.getHeight()));

						}
					}

				});
				// Start the timer
				timer.setInitialDelay(0);
				timer.start();

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});

		// Sleep for a while to allow the timer task to run
		// Thread.sleep((delay + interval) * repeatCount);

	}

	// 테스트 시작 후 프로그램
	public void setTest() throws AWTException {
		Robot robot = new Robot();
		robot.mouseMove(smartphoneGUI.getWidth() / 2, 700);
		startTime = System.currentTimeMillis();
		randomIndex = (int) (Math.random() * 8);
		randomSize = 10 + (int) (Math.random() * 10);

		testAnswerOutput[count][0] = (double) randomIndex;
		testAnswerOutput[count][1] = (double) randomSize;

		System.out.println("index : " + randomIndex + "  /  textsize : " + randomSize + "\n");

		// display
		appIcon.setBackground(randomColor());

		labelText.setText("<html><body style = 'font-size :" + randomSize + "pt'>" + testerNums[randomIndex] + " "
				+ testerTexts[(int) (Math.random() * 2)][randomIndex] + "</body></html>");

	}

	public Color randomColor() {
		int r, g, b;
		r = (int) (Math.random() * 255);
		g = (int) (Math.random() * 255);
		b = (int) (Math.random() * 255);

		return (new Color(r, g, b));
	}

	public static void main(String[] args) throws InterruptedException {
		UXTester prog = new UXTester();
	}

}
