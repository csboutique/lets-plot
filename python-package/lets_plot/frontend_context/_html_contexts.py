#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import os

from ._frontend_ctx import FrontendContext
from ._jupyter_notebook_ctx import JupyterNotebookContext
from ._static_html_page_ctx import StaticHtmlPageContext
from .._global_settings import has_global_value, get_global_bool, HTML_ISOLATED_FRAME


def _create_html_frontend_context(isolated_frame: bool = None, offline: bool = None) -> FrontendContext:
    """

    :param isolated_frame:
    :param offline:
    :return:
    """
    if isolated_frame is None:
        isolated_frame = _use_isolated_frame()

    if isolated_frame:
        return StaticHtmlPageContext(offline)
    else:
        return JupyterNotebookContext(offline)


def _use_isolated_frame() -> bool:
    # check environment
    if has_global_value(HTML_ISOLATED_FRAME):
        return get_global_bool(HTML_ISOLATED_FRAME)

    return _detect_isolated_frame()


def _detect_isolated_frame() -> bool:
    if not _is_IPython_display():
        return True  # isolated HTML page to show somehow

    # Most online notebook platforms are showing cell output in iframe and require
    # a self-contained HTML which includes both:
    # - the script loading JS library and
    # - the script that uses this JS lib to create plot.

    # Try to detect the platform.
    try:
        import google.colab
        return True  # Colab -> iframe
    except ImportError:
        pass

    if os.path.exists("/kaggle/input"):
        return True  # Kaggle -> iframe

    # Check if we're running in an Azure Notebook
    if "AZURE_NOTEBOOKS_HOST" in os.environ:
        return True  # Azure Notebook -> iframe

    # ToDo: other platforms: vscode, nteract, cocalc

    try:
        shell = get_ipython().__class__.__name__
        if shell == 'ZMQInteractiveShell':
            return False  # Jupyter notebook or qtconsole  -> load JS librarty once per notebook
        elif shell == 'TerminalInteractiveShell':
            return True  # Terminal running IPython  -> an isolated HTML page to show somehow
        else:
            return True  # Other type (?)
    except NameError:
        return True  # some other env (even standard Python interpreter) ->  an isolated HTML page to show somehow


def _is_IPython_display() -> bool:
    try:
        from IPython.display import display_html
        return True
    except ImportError:
        return False
