package com.lzf.tenement.util;

public class FormatMatch {
	// 手机号正则表达式（11位数字）
	// 13[0-9]
	// 14[0-9]
	// 15[0-9]
	// 17[013678]
	// 18[0-9]
	private static String phoneRegex1 = "1[3458]\\d\\d{8}";
	private static String phoneRegex2 = "17[013678]\\d{8}";

	public static boolean phone(String phone) {
		if (phone.matches(phoneRegex1) || phone.matches(phoneRegex2)) {
			return true;
		} else {
			return false;
		}
	}
}
