package com.rengh.study.window.utils;

import android.net.Uri;

/**
 * Created by rengh on 18-1-30.
 */

public class VideoUrisUtils {
    private static Uri[] uris = {
            Uri.parse("http://g3com.cp21.ott.cibntv.net/vod/v1/MjYzLzcvODYvbGV0di1ndWcvMTcvMTExMzMwOTkxMC1hdmMtNzc3LWFhYy03Ny0xNTAwMC02Mzc1MjY4LTY4ZTg1NzA3ZTk4N2NhMWQ3MDBjODU5M2YwZmNjMjZlLTE1MDM0NjAxOTY3MjYudHM=?platid=100&amp;splatid=10000&amp;gugtype=6&amp;mmsid=66097865&amp;type=tv_1080p"),
            Uri.parse("http://g3com.cp21.ott.cibntv.net/vod/v1/MjQ4LzI4LzI5L2xldHYtZ3VnLzE3LzExMTk1Mzg2MzUtYXZjLTc3Ny1hYWMtNzctMTUwNDAtNjU2ODM0NC0wOWJjZTQxYTE2MzJiYWI5MWIyOThiYjYxMDY0ZjVkNC0xNTE0NDQ5NTA3MjU0LnRz?platid=100&amp;splatid=10000&amp;gugtype=6&amp;mmsid=66723569&amp;type=tv_1080p"),
            Uri.parse("http://g3com.cp21.ott.cibntv.net/vod/v1/MjU1LzQ2Lzc3L2xldHYtZ3VnLzE3LzExMTk3NDU0MDctYXZjLTc3Ny1hYWMtNzctMTUwMDAtNjQyNDcxMi1mNzBmOGFhMTlmNmQ4YzE1YTM0MTljMDQ2MGExN2MwZS0xNTE1NzQyMjY4Nzc5LnRz?platid=100&amp;splatid=10000&amp;gugtype=6&amp;mmsid=66740468&amp;type=tv_1080p"),
    };

    public static Uri[] getUris() {
        return uris;
    }
}
