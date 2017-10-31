package com.kong.recitewords;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.HtmlPanel;

import javax.swing.*;

/**
 * Created by konglinghai on 2017/10/31.
 */
public class HtmlDialog extends DialogWrapper {

    private HtmlPanel htmlPanel;

    private Editor editor;

    private SaveAction action;


    public HtmlDialog(Editor editor, String text, SaveAction saveAction) {
        super(editor.getProject(), false);
        this.editor = editor;
        this.action = saveAction;
        initPanel(text);
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        return htmlPanel;
    }

    @Override
    protected void init() {
        super.init();
        setTitle("翻译结果");
        setCancelButtonText("取消");
        setOKButtonText("保存");

    }

    protected Action[] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        action.save();
    }

    public void initPanel(String text) {
        htmlPanel = new HtmlPanel();
        htmlPanel.setText(buildHtml(text));
    }

    public String buildHtml(String text) {
        String html = "";
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i] != null && lines[i].length() > 0) {
                html = html + "<p>" + lines[i] + "</p>";
            }
        }
        return "<html><div style=\"width:500px;height:auto\">" + html + "</div></html>";
    }

    interface SaveAction {
        public void save();
    }
}
