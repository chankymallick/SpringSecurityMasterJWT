USE [SPRING_JDBC]
GO
/****** Object:  Table [dbo].[users]    Script Date: 11-07-2025 20:16:26 ******/
    IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[users]') AND type in (N'U'))
DROP TABLE [dbo].[users]
    GO
/****** Object:  Table [dbo].[authorities]    Script Date: 11-07-2025 20:16:26 ******/
    IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[authorities]') AND type in (N'U'))
DROP TABLE [dbo].[authorities]
    GO
/****** Object:  Table [dbo].[authorities]    Script Date: 11-07-2025 20:16:26 ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[authorities](
    [username] [nvarchar](50) NOT NULL,
    [authority] [nvarchar](50) NOT NULL
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[users]    Script Date: 11-07-2025 20:16:26 ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[users](
    [username] [nvarchar](50) NOT NULL,
    [password] [nvarchar](100) NOT NULL,
    [enabled] [bit] NOT NULL,
    PRIMARY KEY CLUSTERED
(
[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
    INSERT [dbo].[authorities] ([username], [authority]) VALUES (N'admin', N'ROLE_ADMIN')
    GO
    INSERT [dbo].[authorities] ([username], [authority]) VALUES (N'mmallick', N'ROLE_ADMIN')
    GO
    INSERT [dbo].[users] ([username], [password], [enabled]) VALUES (N'admin', N'$2a$10$7QJ8Qw1Qw1Qw1Qw1Qw1QwOeQw1Qw1Qw1Qw1Qw1Qw1Qw1Qw1Qw1Qw', 1)
    GO
    INSERT [dbo].[users] ([username], [password], [enabled]) VALUES (N'mmallick', N'$2a$12$fxxYmX6/o5dn0uM0nM0t9OaeWD48U64LCeZ1KXtADCcvMkbLbizx6', 1)
    GO
ALTER TABLE [dbo].[authorities]  WITH CHECK ADD FOREIGN KEY([username])
    REFERENCES [dbo].[users] ([username])
    GO
