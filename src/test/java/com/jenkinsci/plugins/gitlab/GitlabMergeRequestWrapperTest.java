package com.jenkinsci.plugins.gitlab;

import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabMergeRequest;
import org.gitlab.api.models.GitlabNote;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabUser;
import org.jenkinsci.plugins.gitlab.Gitlab;
import org.jenkinsci.plugins.gitlab.GitlabMergeRequestBuilder;
import org.jenkinsci.plugins.gitlab.GitlabMergeRequestWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static mockit.Deencapsulation.invoke;
import static mockit.Deencapsulation.newInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@RunWith(JMockit.class)
public class GitlabMergeRequestWrapperTest {
    @Test
    public void getSortedNotes(
            @Injectable final Gitlab gitlab,
            @Injectable final GitlabAPI api,
            @Injectable final GitlabMergeRequest mergeRequest,
            @Injectable final GitlabMergeRequestBuilder builder,
            @Injectable final GitlabProject project,
            @Injectable final GitlabUser user
    ) throws Exception {
        final GitlabNote note1 = new GitlabNote();
        final GitlabNote note2 = new GitlabNote();
        final GitlabNote note3 = new GitlabNote();
        DateFormat df = new SimpleDateFormat("yyyy-MM-DD");
        note1.setCreatedAt(df.parse("2014-01-01"));
        note2.setCreatedAt(df.parse("2014-02-02"));
        note3.setCreatedAt(df.parse("2014-03-03"));

        new NonStrictExpectations() {{
            List<GitlabNote> notes = Arrays.asList(new GitlabNote[] {note1, note3, note2});

            api.getAllNotes(mergeRequest); result = new ArrayList<GitlabNote>(notes);
            api.getProject(anyInt); result = project;
            builder.getGitlab(); result = gitlab;
            gitlab.get(); result = api;
            mergeRequest.getAuthor(); result = user;
        }};

        GitlabMergeRequestWrapper wrapper = newInstance(GitlabMergeRequestWrapper.class.getName(), mergeRequest, builder, project);
        List<GitlabNote> sortedNotes = invoke(wrapper, "getSortedNotes", mergeRequest, api);

        assertThat(sortedNotes, contains(note3, note2, note1));
    }
}
