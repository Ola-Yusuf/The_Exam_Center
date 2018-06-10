package com.example.android.worldquiz;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    String[][] question, selectedOption;

    String[] markGuide;

    String userName;

    int currentQuestion, totalQuestion, markForEachQuestion;

    double totalScore;

    boolean sendResultAsMail,timeRunning;

    long START_TIME_IN_MILLIS, mTimeLeftInMillis;

    CountDownTimer mCountDownTimer;

    RadioButton[] rb, idOfSelectedRadioButtonForQuestion;

    TextView instruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_login);
        initializer();
    }

    @SuppressLint("StringFormatInvalid")
    public String initializer(){

        START_TIME_IN_MILLIS = this.getResources().getInteger(R.integer.durationOfExamInMillisec);

        mTimeLeftInMillis = START_TIME_IN_MILLIS;

        instruction = (TextView) findViewById(R.id.instruction);

        instruction.setText(getString(R.string.instruction, convertMilliSecToMinuteAndSeconds()));

        convertQuestionToMultiDimArray();

        //get the stringArray containing answers to the question and store in markingGuide
        markGuide = getArrayFromXmlFile(R.array.answers);

        markForEachQuestion = this.getResources().getInteger(R.integer.markAllocateForEachQuestion);

        userName = "";

        currentQuestion = 0;

        totalScore =0.0;

        sendResultAsMail = false;

        return null;
    }

    /**
     * This method access the stringArray tag named "questions"  in string xml file
     * convert the it into 2Dimensional Array
     * initialize length of array: selectedOption, radiobuttonOption and idOfSelectedRadioButton
     */
    public void convertQuestionToMultiDimArray(){

        //get the stringArray named "questions"  from xml file
        String[] singleArrayExamQuestions = getArrayFromXmlFile(R.array.questions);

        //split the 1st content of the stringArray obtain from xml file base of the Format
        //to get the total length of separable content in each <item> tag
        String[] count = singleArrayExamQuestions[0].split("\\|");

        //initialize the size of the 2D array corresponding to the stringArray in xml file
        question = new String[singleArrayExamQuestions.length][count.length];

        //initialize the size of the 2D array to track the selected options
        selectedOption = new String[singleArrayExamQuestions.length][count.length];

        //initialize array of number of available options
        rb = new RadioButton[count.length-1];

        //initialize array to store id_of_selected_radio_button
        idOfSelectedRadioButtonForQuestion = new RadioButton[singleArrayExamQuestions.length];

        //initialize the total Number of questions available
        totalQuestion = question.length;

        //loop the content of extracted stringArray and store properly in the 2D array
        for(int i = 0; i<question.length; i++){
            //split each extracted StringArray items
            String[] separatedArray = singleArrayExamQuestions[i].split("\\|");

            int x = 0;

            do{
                    //store in corresponding position in 2D array
                    question[i][x] = separatedArray[x];
                x++;

            }while (x < separatedArray.length); //condition to ensure all content are stored
        }
    }

    /**
     * get Array form xml file
     */
    public String[] getArrayFromXmlFile(int targetArray){
        return getResources().getStringArray(targetArray);
    }

    /**
     * For each question Track options selected by the user
     * @param qNum
     * @param chosen
     */
    public void setSelectedOption(int qNum, String chosen) {
        selectedOption[qNum][0] = qNum+"";
        selectedOption[qNum][1] = chosen;
    }

    /**
     * compare selectedOption with the marking guild
     * the update the value of Total score.
     */
    public void markExam(){

        for (String[] q: selectedOption) {
                if (q[1]!=null && q[1].equalsIgnoreCase(markGuide[Integer.parseInt(q[0])]))
                    totalScore += 10.0;
        }
    }

    /**
     * Toast to show notification
     * @param c is the notification to be shown
     */
    public void showToast(String c){
        Toast.makeText(this, c , Toast.LENGTH_SHORT).show();
    }

    /**
     * store the id of the selected radio button
     * store the text of the selected option
     * change the style of the option selected by the user
     * @param chbtn is the selected option
     * @return null
     */
    public String processSelectedOption(RadioButton chbtn){
        idOfSelectedRadioButtonForQuestion[currentQuestion] = chbtn;
        setSelectedOption(currentQuestion, chbtn.getText().toString());
        chbtn.setTypeface(null, Typeface.BOLD_ITALIC);
        return null;
    }

    /**
     * set the style of unselected options to normal
     * @param unch1 unselected radio button
     * @param unch2 unselected radio button
     * @param unch3 unselected radio button
     * @return
     */
    public String unboldUnselectedOption(RadioButton unch1, RadioButton unch2, RadioButton unch3 ){
        unch1.setTypeface(null, Typeface.NORMAL);
        unch2.setTypeface(null, Typeface.NORMAL);
        unch3.setTypeface(null, Typeface.NORMAL);
        return null;
    }

    /**
     *set the unchecked radio buttons text style to default
     * @return
     */
    public String unboldAllOption(){
        rb =  radioButtonsForOptions();
        rb[0].setTypeface(null, Typeface.NORMAL);
        rb[1].setTypeface(null, Typeface.NORMAL);
        rb[2].setTypeface(null, Typeface.NORMAL);
        rb[3].setTypeface(null, Typeface.NORMAL);
        return null;
    }

    /**
     * store the radioButton used to display options
     * for each question in an array
     * @return array of RadioButton
     */
    public RadioButton[] radioButtonsForOptions(){
        RadioButton[] rbOp = new RadioButton[rb.length];
        //require to import the RadioButton class
        rbOp[0] = (RadioButton) findViewById(R.id.radio_button_0);
        rbOp[1] = (RadioButton) findViewById(R.id.radio_button_1);
        rbOp[2] = (RadioButton) findViewById(R.id.radio_button_2);
        rbOp[3] = (RadioButton) findViewById(R.id.radio_button_3);
        return rbOp;
    }

    /**
     * Detecting the previous selected option
     * @return null
     */
    public String getPreviousSelection(){
        unboldAllOption();
        idOfSelectedRadioButtonForQuestion[currentQuestion].setChecked(true);
        processSelectedOption(idOfSelectedRadioButtonForQuestion[currentQuestion]);
        return null;
    }

    /**
     * Detecting chosen answer for current question inorder to change the style
     * @param view
     */
    public void chosenAnswer(View view){
        rb =  radioButtonsForOptions();
        //is the current radio button now checked?
        boolean  checked = ((RadioButton) view).isChecked();

        //now check which radio button is selected
        switch(view.getId()){

            case R.id.radio_button_0:
                if(checked)
                    processSelectedOption(rb[0]);
                    unboldUnselectedOption(rb[1], rb[2], rb[3]);
                break;

            case R.id.radio_button_1:
                if(checked)
                    processSelectedOption(rb[1]);
                    unboldUnselectedOption(rb[0], rb[2], rb[3]);
                break;

            case R.id.radio_button_2:
                if(checked)
                    processSelectedOption(rb[2]);
                    unboldUnselectedOption(rb[1], rb[0], rb[3]);
                break;

            case R.id.radio_button_3:
                if(checked)
                    processSelectedOption(rb[3]);
                unboldUnselectedOption(rb[1], rb[2], rb[0]);
                break;

        }
    }

    /**
     * end the exam and call displayResult method
     */
    public void endExam(){
         mCountDownTimer.cancel();
         markExam();
        this.setContentView(R.layout.report_sheet); //move to the Report sheet layout/interface
        displayResult();
    }

    /**
     * submit
     * @param view
     */
    public void submit(View view){
        endExam();
    }

    /**
     * Display the corresponding options to a particular question
     * Remember question[x][0] contain the question
     * @param x is number of the question
     */
    public void displayOptionsForQuestion(int x){
        rb =  radioButtonsForOptions();
        rb[0].setText(question[x][1]);
        rb[1].setText(question[x][2]);
        rb[2].setText(question[x][3]);
        rb[3].setText(question[x][4]);
    }

    /**
     * Display question at number x
     * @param  x
     */
    public void displayQuestion(int x){
        TextView que = (TextView) findViewById(R.id.question);
        que.setText(question[x][0]);
    }

    /**
     * when next button is click
     * selecting the nextQuestion by adding 1 to the value of current question
     * @param view
     */
    public void nextQuestion(View view){
        //check if the user is Not at the last available question
        if(currentQuestion < totalQuestion-1) {
            currentQuestion += 1;
            //check if the question has been previously answered by the user
            if(selectedOption[currentQuestion][1] != null){
                //change style of the previously selected answer
                getPreviousSelection();
            }else {
                //make the style of the options to be normal
                RadioGroup myAns = (RadioGroup) findViewById(R.id.btngroup);
                myAns.clearCheck();
                unboldAllOption();
            }
            //show question and corresponding options
            pickExercise();

        } else {
            //Notify user that s/he is currently at the last available question
            showToast(getString(R.string.lastQuestionToast));
        }
    }

    /**
     * when previous button is click
     * selecting the previous question by subtracting 1 from the value of current question.
     * @param view
     */

    public void prevQuestion(View view){
        //check if user is Not on the first question
        if(currentQuestion > 0) {
            //decrease value of currentQuestion
            currentQuestion -= 1;
            //check if the question is previously answered
            if(selectedOption[currentQuestion][1] != null){
                //get the previously selected answer and change the style
            getPreviousSelection();

            }else {
                //if no answer is previously selected change the style of the options to normal
                RadioGroup myAns = (RadioGroup) findViewById(R.id.btngroup);
                myAns.clearCheck();
                unboldAllOption();
            }
            //show question and corresponding options
            pickExercise();

        } else {
            //notify the user s/he is currently on the first question. i.e No previous
            showToast(getString(R.string.firstQuestionToast));
        }
    }

    /**
     * display the login page and reset all necessary values
     * @param view
     */
    public void tryAgain(View view){
        //move to the login layout/ interface
        this.setContentView(R.layout.exam_login);
        initializer();
    }

    /**
     * Get userName and Start Exam
     * @param view
     */
    public void start(View view){
        //get the users name
        EditText name = (EditText) findViewById(R.id.userName);
        String nameInput =name.getText().toString();

        if((nameInput.length()>4) && (nameInput.length()<11) ) {
            userName = nameInput.toUpperCase();
            this.setContentView(R.layout.question_paper); //move to the exam layout/ interface
            pickExercise();
            startTimer(); //start the countdown
        }else {
            //Notify the user about the structure of the user name
            showToast(getString(R.string.userNameEntryToast));
        }

    }

    private String startTimer() {
         mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeRunning = false;
                showToast(getString(R.string.timeUpToast));
                endExam();
            }

        }.start();
        timeRunning = true;
        return null;
    }

    /**
     * convert milliseconds to Minute and seconds
     * @return minute and second in string
     */
    public String convertMilliSecToMinuteAndSeconds(){
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
       return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    /**
     * update the countdown time displayed
     * @return
     */
    @SuppressLint("StringFormatInvalid")
    private String updateCountDownText() {
        TextView mTextViewCountDown = (TextView) findViewById(R.id.timer);
        mTextViewCountDown.setText(getString(R.string.countDown, convertMilliSecToMinuteAndSeconds()));
        return  null;
    }

    /**
     * selecting question to be viewed by user.
     */
    public void pickExercise(){
          displayOptionsForQuestion(currentQuestion);
          displayQuestion(currentQuestion);
          updateStage();
    }

    /**
     * update and the current stage of th exam
     */
    @SuppressLint("StringFormatInvalid")
    public void updateStage(){
        int cNum = currentQuestion + 1;
        TextView stage = (TextView) findViewById(R.id.stage);
        stage.setText(getString(R.string.stage, cNum, totalQuestion));
    }


    /**
     * select and image from drawable folder based on users score
     * display result information and total time spent
     *
     */
    @SuppressLint({"StringFormatMatches","StringFormatInvalid"})
    public void displayResult(){
        TextView  sSheet= (TextView) findViewById(R.id.scoreSheet);
        ImageView rImage = (ImageView) findViewById(R.id.reportImage);

            if(totalScore<30.0){
                rImage.setImageResource(R.drawable.fail);
            }
            else if(totalScore>=30.0 && totalScore<50.0){
                rImage.setImageResource(R.drawable.pass);
            }
            else if(totalScore>=50.0 && totalScore<80.0){
                rImage.setImageResource(R.drawable.average);
            }
            else if(totalScore>=80.0) {
                rImage.setImageResource(R.drawable.excellent);
            }

        if(!timeRunning){
                //if time is up that is user used all the allocated time
            mTimeLeftInMillis = this.getResources().getInteger(R.integer.durationOfExamInMillisec);
        }else {
            //calculate time used by the user for the exam.
            mTimeLeftInMillis = START_TIME_IN_MILLIS - mTimeLeftInMillis;
        }

        String finalReportMessage = getString(R.string.report, userName, totalScore);
                finalReportMessage+= getString(R.string.timeUsed, convertMilliSecToMinuteAndSeconds());

        sSheet.setText(finalReportMessage);

    }

}
