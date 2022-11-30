import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;

public class DataStreamFrame extends JFrame
{
    JPanel mainPnl, titlePnl, displayPnl, cmdPnl, inputPnl, originalPnl, filteredPnl;
    JLabel titleLbl, sourceLbl, sourceDirLbl, sourceFileLbl, searchLbl, searchWordLbl, originalLbl, filteredLbl;
    JScrollPane scrollerOrig, scrollerFilt;
    JTextArea originalTA, filteredTA;
    JButton quitBtn, sourceBtn, searchBtn, searchWordBtn;
    String searchTerm;
    File selectedFile;
    boolean source = false, search = false;
    ArrayList<String> filteredLines = new ArrayList<>();

    public DataStreamFrame() throws HeadlessException
    {
        setTitle("Data Stream Processing");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int scrnHeight = screenSize.height;
        int scrnWidth = screenSize.width;
        setSize(scrnWidth*3/4, scrnHeight*3/4);
        setLocation(scrnWidth/8, scrnHeight/8);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());
        add(mainPnl);

        createTitlePanel();
        createCommandPanel();
        createDisplayPanel();

        setVisible(true);
    }
    private void createCommandPanel()
    {
        cmdPnl = new JPanel();
        cmdPnl.setLayout(new GridLayout(1,2));

        quitBtn = new JButton("Quit");
        quitBtn.setFont(new Font("Bold", Font.BOLD, 18));

        searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Bold", Font.BOLD, 18));
        searchBtn.setEnabled(false);

        quitBtn.addActionListener((ActionEvent ae) ->
        {
            String quit = "Are you sure you want to quit?";
            if (JOptionPane.showConfirmDialog(null, quit,"Quit", JOptionPane.YES_NO_OPTION)==0) {
                System.exit(0);
            }
        });
        searchBtn.addActionListener((ActionEvent ae) ->
        {
            filteredTA.setText("");
            runDataStream();
            for (String line : filteredLines) {
                filteredTA.append(line + "\n");
            }
        });

        cmdPnl.add(searchBtn);
        cmdPnl.add(quitBtn);

        mainPnl.add(cmdPnl, BorderLayout.SOUTH);
    }
    private void createTitlePanel()
    {
        titlePnl = new JPanel();

        titleLbl = new JLabel("Data Stream Processor", JLabel.CENTER);
        titleLbl.setVerticalTextPosition(JLabel.TOP);
        titleLbl.setHorizontalTextPosition(JLabel.CENTER);
        titleLbl.setFont(new Font("Bold Italic", Font.BOLD | Font.ITALIC, 36));

        titlePnl.add(titleLbl);

        mainPnl.add(titlePnl, BorderLayout.NORTH);
    }
    private void createDisplayPanel()
    {
        displayPnl = new JPanel();
        displayPnl.setLayout(new GridLayout(1,3));
        displayPnl.setBorder(new EmptyBorder(50,50,50,50));

        createInputPanel();
        createOriginalPanel();
        createFilteredPanel();

        displayPnl.add(inputPnl);
        displayPnl.add(originalPnl);
        displayPnl.add(filteredPnl);

        mainPnl.add(displayPnl);
    }
    private void createInputPanel()
    {
        inputPnl = new JPanel();
        inputPnl.setLayout(new BoxLayout(inputPnl, BoxLayout.Y_AXIS));

        sourceLbl = new JLabel("Source File:");
        sourceDirLbl = new JLabel();
        sourceFileLbl = new JLabel("No source file chosen.");
        sourceDirLbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        sourceFileLbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        sourceBtn = new JButton("Choose Source File");

        searchLbl = new JLabel("Search Term:");
        searchWordLbl = new JLabel("");
        searchWordLbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        searchWordBtn = new JButton("Enter Search Term");

        sourceBtn.addActionListener((ActionEvent ae) ->
        {
            JFileChooser chooser = new JFileChooser();
            String rec = "";

            filteredLines.clear();

            try
            {
                File workingDirectory = new File(System.getProperty("user.dir"));
                chooser.setCurrentDirectory(workingDirectory);

                if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                {
                    originalTA.setText("");
                    selectedFile = chooser.getSelectedFile();
                    Path file = selectedFile.toPath();
                    InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    while(reader.ready())
                    {
                        rec = reader.readLine();
                        originalTA.append(rec + "\n");
                    }
                    reader.close();
                    sourceDirLbl.setText(selectedFile.getPath().replace(selectedFile.getName(),""));
                    sourceFileLbl.setText(selectedFile.getName());
                    searchWordLbl.setText("");
                    filteredTA.setText("");
                    source = true;
                    if(source&&search) {
                        searchBtn.setEnabled(true);
                    }
                }
            }
            catch (FileNotFoundException e)
            {
                System.out.println("File not found!!!");
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });

        searchWordBtn.addActionListener((ActionEvent ae) ->
        {
            searchTerm = JOptionPane.showInputDialog(null,"Please enter a search term.","Search Term",JOptionPane.QUESTION_MESSAGE);
            searchWordLbl.setText(searchTerm);
            filteredLines.clear();
            search = true;
            if(source&&search) {
                searchBtn.setEnabled(true);
            }
        });

        inputPnl.add(sourceLbl);
        inputPnl.add(sourceDirLbl);
        inputPnl.add(sourceFileLbl);
        inputPnl.add(sourceBtn);
        inputPnl.add(new JLabel(" "));
        inputPnl.add(searchLbl);
        inputPnl.add(searchWordLbl);
        inputPnl.add(searchWordBtn);
    }
    private void createOriginalPanel()
    {
        originalPnl = new JPanel();
        originalPnl.setLayout(new BoxLayout(originalPnl, BoxLayout.Y_AXIS));

        originalLbl = new JLabel("Original Text File:");
        originalPnl.add(originalLbl);

        originalTA = new JTextArea(10,40);
        originalTA.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        originalTA.setEditable(false);
        scrollerOrig = new JScrollPane(originalTA);

        originalPnl.add(scrollerOrig);
    }
    private void createFilteredPanel()
    {
        filteredPnl = new JPanel();
        filteredPnl.setLayout(new BoxLayout(filteredPnl, BoxLayout.Y_AXIS));

        filteredLbl = new JLabel("Filtered Text:");
        filteredPnl.add(filteredLbl);

        filteredTA = new JTextArea(10,40);
        filteredTA.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        filteredTA.setEditable(false);
        scrollerFilt = new JScrollPane(filteredTA);

        filteredPnl.add(scrollerFilt);
    }
    private void runDataStream()
    {
        try (Stream<String> lines = Files.lines(selectedFile.toPath())) {
            lines.filter(a -> a.contains(searchTerm)).forEach(filteredLines::add);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}